package kr.ac.kumoh.likelion.gugu.domain.community.repo;

import kr.ac.kumoh.likelion.gugu.domain.community.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostIdAndDeletedFalseOrderByCreatedAtAsc(Long postId, Pageable pageable);

    long countByPostIdAndDeletedFalse(Long postId);
}