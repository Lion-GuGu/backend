package kr.ac.kumoh.likelion.gugu.community.service;

import kr.ac.kumoh.likelion.gugu.community.event.CommentCreatedEvent;
import kr.ac.kumoh.likelion.gugu.community.model.Comment;
import kr.ac.kumoh.likelion.gugu.community.model.Post;
import kr.ac.kumoh.likelion.gugu.community.repo.CommentRepository;
import kr.ac.kumoh.likelion.gugu.community.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository postRepo;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Long create(Long postId, Long authorId, String content, Long parentId) {
        Post post = postRepo.findById(postId).orElseThrow();
        if (post.isDeleted()) throw new IllegalStateException("삭제된 글입니다.");

        if (parentId != null) {
            Comment parent = commentRepo.findById(parentId).orElseThrow();
            if (!parent.getPostId().equals(postId)) {
                throw new IllegalArgumentException("부모 댓글/게시글 불일치");
            }
        }

        Comment c = Comment.builder()
                .postId(postId)
                .authorId(authorId)
                .parentId(parentId)
                .content(content)
                .build();

        Long id = commentRepo.save(c).getId();

        // 댓글 생성 이벤트 발행 → 포인트 적립 리스너가 처리
        publisher.publishEvent(new CommentCreatedEvent(authorId, post.getId(), post.getCategory(), post.getAuthorId()));

        return id;
    }

    @Transactional(readOnly = true)
    public Page<Comment> listByPost(Long postId, Pageable pageable) {
        return commentRepo.findByPostIdAndDeletedFalseOrderByCreatedAtAsc(postId, pageable);
    }

    @Transactional
    public void delete(Long commentId, Long actorId) {
        Comment c = commentRepo.findById(commentId).orElseThrow();
        if (!c.getAuthorId().equals(actorId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }
        c.setDeleted(true);
    }
}