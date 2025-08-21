package kr.ac.kumoh.likelion.gugu.domain.community.repo;

import kr.ac.kumoh.likelion.gugu.domain.community.model.Post;
import kr.ac.kumoh.likelion.gugu.domain.community.model.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCategoryAndDeletedFalseOrderByCreatedAtDesc(PostCategory category, Pageable pageable);

    Page<Post> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
}