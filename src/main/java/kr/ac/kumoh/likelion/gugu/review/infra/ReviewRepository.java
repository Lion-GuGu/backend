package kr.ac.kumoh.likelion.gugu.review.infra;

import kr.ac.kumoh.likelion.gugu.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 사용자가 받은 리뷰 목록 (최신순)
    Page<Review> findByRevieweeIdOrderByCreatedAtDesc(Long revieweeId, Pageable pageable);

    // 리뷰어가 해당 리뷰이에게 리뷰를 이미 작성했는지 확인 (중복 방지용)
    boolean existsByReviewerIdAndRevieweeId(Long reviewerId, Long revieweeId);

    // 평균 평점
    @Query("select coalesce(avg(r.rating), 0) from Review r where r.revieweeId = :revieweeId")
    double avgRatingByReviewee(Long revieweeId);

    // 총 리뷰 개수
    long countByRevieweeId(Long revieweeId);

    // 별점 분포 (1~5별 개수)
    @Query("select r.rating as rating, count(r) as cnt " +
            "from Review r where r.revieweeId = :revieweeId " +
            "group by r.rating")
    List<Object[]> ratingBuckets(Long revieweeId);
}
