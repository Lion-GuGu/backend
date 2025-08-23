package kr.ac.kumoh.likelion.gugu.community.service;

import kr.ac.kumoh.likelion.gugu.community.event.PostCreatedEvent;
import kr.ac.kumoh.likelion.gugu.community.model.Post;
import kr.ac.kumoh.likelion.gugu.community.model.PostCategory;
import kr.ac.kumoh.likelion.gugu.community.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 게시글 생성
     */
    @Transactional
    public Long create(Long authorId, PostCategory category, String title, String content) {
        Post p = Post.builder()
                .authorId(authorId)
                .category(category)
                .title(title)
                .content(content)
                .build();
        Post saved = postRepo.save(p);

        log.info("[POST] created id={}, authorId={}", saved.getId(), authorId);
        eventPublisher.publishEvent(new PostCreatedEvent(authorId, saved.getId()));
        log.info("[EVENT] PostCreatedEvent published postId={}, authorId={}", saved.getId(), authorId);

        return saved.getId();
    }

    /**
     * 게시글 상세 조회(+조회수 증가)
     * - 프론트 상세 페이지 진입 시 이 메서드를 사용하세요.
     * - 동시성 유실 방지를 위해 DB 원자 증가 쿼리를 사용합니다.
     */
    @Transactional
    public Post view(Long postId) {
        Post p = postRepo.findById(postId).orElseThrow(() -> new IllegalStateException("게시글이 존재하지 않습니다."));
        ensureNotDeleted(p);

        // 원자적 증가 (Repository에 @Modifying 쿼리 필요)
        postRepo.increaseViewCount(postId);

        // 응답 객체에도 증가가 반영되도록 메모리 값 갱신
        p.setViewCount(p.getViewCount() + 1);

        // 더티체킹으로 flush되지만, viewCount는 쿼리로 증가시켰으므로 추가 save 불필요
        return p;
    }

    /**
     * 게시글 단순 조회(조회수 증가 없음)
     * - 수정/삭제 등의 내부 검증용으로 사용합니다.
     */
    @Transactional(readOnly = true)
    public Post findOne(Long postId) {
        Post p = postRepo.findById(postId).orElseThrow(() -> new IllegalStateException("게시글이 존재하지 않습니다."));
        ensureNotDeleted(p);
        return p;
    }

    /**
     * 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<Post> list(PostCategory category, Pageable pageable) {
        if (category == null) {
            return postRepo.findByDeletedFalseOrderByCreatedAtDesc(pageable);
        }
        return postRepo.findByCategoryAndDeletedFalseOrderByCreatedAtDesc(category, pageable);
    }

    /**
     * 게시글 수정 (작성자만)
     * - 조회수 증가 없음
     */
    @Transactional
    public void update(Long postId, Long actorId, String title, String content) {
        Post p = findOne(postId); // 조회수 증가 없는 단순 조회
        ensureAuthor(p, actorId);
        p.setTitle(title);
        p.setContent(content);
        // JPA 더티체킹으로 커밋 시 자동 반영
    }

    /**
     * 게시글 삭제 (작성자만) - 소프트 삭제
     * - 조회수 증가 없음
     */
    @Transactional
    public void delete(Long postId, Long actorId) {
        Post p = findOne(postId); // 조회수 증가 없는 단순 조회
        ensureAuthor(p, actorId);
        p.setDeleted(true);
        // JPA 더티체킹으로 커밋 시 자동 반영
    }

    // --------- helpers ---------

    private void ensureNotDeleted(Post p) {
        if (p.isDeleted()) {
            throw new IllegalStateException("삭제된 게시글입니다.");
        }
    }

    private void ensureAuthor(Post p, Long actorId) {
        if (!p.getAuthorId().equals(actorId)) {
            throw new SecurityException("작성자만 수정/삭제할 수 있습니다.");
        }
    }
}
