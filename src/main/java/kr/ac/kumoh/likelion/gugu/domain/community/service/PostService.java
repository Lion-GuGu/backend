package kr.ac.kumoh.likelion.gugu.domain.community.service;

import kr.ac.kumoh.likelion.gugu.domain.community.model.Post;
import kr.ac.kumoh.likelion.gugu.domain.community.model.PostCategory;
import kr.ac.kumoh.likelion.gugu.domain.community.repo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;

    @Transactional
    public Long create(Long authorId, PostCategory category, String title, String content) {
        Post p = Post.builder()
                .authorId(authorId)
                .category(category)
                .title(title)
                .content(content)
                .build();
        return postRepo.save(p).getId();
    }

    @Transactional(readOnly = true)
    public Post get(Long postId) {
        Post p = postRepo.findById(postId).orElseThrow();
        if (p.isDeleted()) throw new IllegalStateException("삭제된 게시글입니다.");
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