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

    @Transactional
    public Long create(Long authorId, PostCategory category, String title, String content) {
        Post p = Post.builder()
                .authorId(authorId)
                .category(category)
                .title(title)
                .content(content)
                .build();
        Post saved = postRepo.save(p);

        log.info("[POST] created id={}, authorId={}", saved.getId(), authorId);  // 👈 발행 전 로그
        eventPublisher.publishEvent(new PostCreatedEvent(authorId, saved.getId()));
        log.info("[EVENT] PostCreatedEvent published postId={}, authorId={}", saved.getId(), authorId); // 👈 발행 직후 로그

        return saved.getId();
    }

    @Transactional  // readOnly = true 제거
    public Post get(Long postId) {
        Post p = postRepo.findById(postId).orElseThrow();
        if (p.isDeleted()) throw new IllegalStateException("삭제된 게시글입니다.");

        p.setViewCount(p.getViewCount() + 1);  // 조회수 증가
        postRepo.save(p);  // 저장

        return p;
    }

    @Transactional(readOnly = true)
    public Page<Post> list(PostCategory category, Pageable pageable) {
        if (category == null) {
            return postRepo.findByDeletedFalseOrderByCreatedAtDesc(pageable);
        }
        return postRepo.findByCategoryAndDeletedFalseOrderByCreatedAtDesc(category, pageable);
    }

    @Transactional
    public void update(Long postId, Long actorId, String title, String content) {
        Post p = get(postId);
        if (!p.getAuthorId().equals(actorId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }
        p.setTitle(title);
        p.setContent(content);
    }

    @Transactional
    public void delete(Long postId, Long actorId) {
        Post p = get(postId);
        if (!p.getAuthorId().equals(actorId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }
        p.setDeleted(true);
    }
}