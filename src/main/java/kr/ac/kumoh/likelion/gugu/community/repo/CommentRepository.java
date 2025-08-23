package kr.ac.kumoh.likelion.gugu.community.repo;

import kr.ac.kumoh.likelion.gugu.community.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 목록 (삭제 안 된 것만, 생성일 오름차순)
    Page<Comment> findByPostIdAndDeletedFalseOrderByCreatedAtAsc(Long postId, Pageable pageable);

    // 게시글 내 유효 댓글 수
    long countByPostIdAndDeletedFalse(Long postId);

    // 현재 게시글에서 이미 채택된 댓글 있나?
    Optional<Comment> findByPostIdAndAcceptedTrue(Long postId);

    // 성능/원자성 향상을 위한 벌크 해제 쿼리
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Comment c set c.accepted = false where c.postId = :postId and c.accepted = true and c.id <> :exceptCommentId")
    int bulkUnacceptOthers(Long postId, Long exceptCommentId);
}
