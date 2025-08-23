package kr.ac.kumoh.likelion.gugu.community.repo;

import kr.ac.kumoh.likelion.gugu.community.model.Post;
import kr.ac.kumoh.likelion.gugu.community.model.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCategoryAndDeletedFalseOrderByCreatedAtDesc(PostCategory category, Pageable pageable);

    Page<Post> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    // 조회수 증가 쿼리 추가 (수정)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = :id")
    int increaseViewCount(@Param("id") Long id);
}