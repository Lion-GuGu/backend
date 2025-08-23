package kr.ac.kumoh.likelion.gugu.community.service;

import kr.ac.kumoh.likelion.gugu.community.event.AnswerAcceptedEvent;
import kr.ac.kumoh.likelion.gugu.community.event.CommentCreatedEvent;
import kr.ac.kumoh.likelion.gugu.community.model.Comment;
import kr.ac.kumoh.likelion.gugu.community.model.Post;
import kr.ac.kumoh.likelion.gugu.community.model.PostCategory;
import kr.ac.kumoh.likelion.gugu.community.repo.CommentRepository;
import kr.ac.kumoh.likelion.gugu.community.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository postRepo;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 댓글 생성
     * - 삭제된 글에는 불가
     * - 대댓글(parentId)인 경우, 부모 댓글의 postId 일치 검증
     * - 저장 후 CommentCreatedEvent 발행 (포인트 적립 등)
     *
     * @return 생성된 댓글 ID
     */
    @Transactional
    public Long create(Long postId, Long authorId, String content, Long parentId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));
        if (post.isDeleted()) { // boolean 게터는 isDeleted()
            throw new IllegalStateException("삭제된 글입니다.");
        }

        if (parentId != null) {
            Comment parent = commentRepo.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다: " + parentId));
            if (!parent.getPostId().equals(postId)) {
                throw new IllegalArgumentException("부모 댓글/게시글 불일치");
            }
            if (parent.isDeleted()) { // boolean 게터는 isDeleted()
                throw new IllegalStateException("삭제된 댓글에는 대댓글을 달 수 없습니다.");
            }
        }

        Comment c = Comment.builder()
                .postId(postId)
                .authorId(authorId)
                .parentId(parentId)
                .content(content)
                .deleted(false)
                .accepted(false)
                .build();

        Comment saved = commentRepo.save(c);

        // 이벤트 발행 (AFTER_COMMIT 리스너에서 포인트 적립 등 처리)
        eventPublisher.publishEvent(
                new CommentCreatedEvent(authorId, post.getId(), post.getCategory(), post.getAuthorId())
        );

        return saved.getId();
    }

    /**
     * 특정 게시글의 댓글 목록 (삭제되지 않은 것만, 생성일 오름차순)
     */
    @Transactional(readOnly = true)
    public Page<Comment> listByPost(Long postId, Pageable pageable) {
        return commentRepo.findByPostIdAndDeletedFalseOrderByCreatedAtAsc(postId, pageable);
    }

    /**
     * 댓글 삭제 (작성자 본인만 가능)
     * - 소프트 삭제: deleted = true
     */
    @Transactional
    public void delete(Long commentId, Long actorId) {
        Comment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId));
        if (!c.getAuthorId().equals(actorId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }
        c.setDeleted(true);
        // 트랜잭션 종료 시 flush
    }

    /**
     * 답변 채택
     * - '질문' 카테고리의 글에서만 가능
     * - 질문 작성자만 채택 가능
     * - 기존 채택 댓글이 있으면 해제 후 새로 채택
     * - 채택 완료 후 AnswerAcceptedEvent 발행 (답변자에게 포인트 지급 등)
     */
    @Transactional
    public void acceptAnswer(Long postId, Long commentId, Long actorUserId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));

        if (post.getCategory() != PostCategory.QUESTION) {
            throw new IllegalStateException("채택은 '질문' 카테고리에만 가능합니다.");
        }

        if (!post.getAuthorId().equals(actorUserId)) {
            throw new SecurityException("질문 작성자만 채택할 수 있습니다.");
        }

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId));

        if (!comment.getPostId().equals(postId)) {
            throw new IllegalArgumentException("댓글이 해당 질문 글에 속하지 않습니다.");
        }
        if (comment.isDeleted()) { // boolean 게터는 isDeleted()
            throw new IllegalStateException("삭제된 댓글은 채택할 수 없습니다.");
        }

        // 기존 채택 댓글이 있으면 해제
        Optional<Comment> prevAccepted = commentRepo.findByPostIdAndAcceptedTrue(postId);
        if (prevAccepted.isPresent()) {
            Comment prev = prevAccepted.get();
            if (!prev.getId().equals(commentId)) {
                prev.setAccepted(false);
                commentRepo.save(prev);
            }
        }

        // 현재 댓글을 채택
        if (!comment.isAccepted()) { // boolean 게터는 isAccepted()
            comment.setAccepted(true);
            commentRepo.save(comment);
        }

        // 이벤트 발행 (AFTER_COMMIT 리스너에서 포인트 적립 처리)
        eventPublisher.publishEvent(
                new AnswerAcceptedEvent(
                        postId,
                        commentId,
                        post.getAuthorId(),     // 질문자
                        comment.getAuthorId()   // 답변자(채택된 댓글 작성자)
                )
        );
    }
}
