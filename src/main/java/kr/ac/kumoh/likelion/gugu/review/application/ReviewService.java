package kr.ac.kumoh.likelion.gugu.review.application;

import kr.ac.kumoh.likelion.gugu.review.domain.Review;
import kr.ac.kumoh.likelion.gugu.review.infra.ReviewRepository;
import kr.ac.kumoh.likelion.gugu.review.web.dto.ReviewStatsResponse;
import kr.ac.kumoh.likelion.gugu.user.domain.User;
import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long create(Long reviewerId, Long revieweeId, int rating, String content) {
        // 자기 자신에게 리뷰 금지
        if (reviewerId.equals(revieweeId)) {
            throw new IllegalArgumentException("자기 자신에게는 리뷰를 작성할 수 없습니다.");
        }

        // 대상 유저 존재 확인
        User reviewee = userRepository.findById(revieweeId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다: " + revieweeId));

        // 중복 방지: 같은 리뷰어→같은 리뷰이 1회 제한
        if (reviewRepository.existsByReviewerIdAndRevieweeId(reviewerId, reviewee.getId())) {
            throw new IllegalStateException("이미 해당 사용자에게 리뷰를 작성했습니다.");
        }

        Review saved = reviewRepository.save(
                Review.builder()
                        .reviewerId(reviewerId)
                        .revieweeId(reviewee.getId())
                        .rating(rating)
                        .content(content)
                        .build()
        );
        return saved.getId();
    }

    @Transactional
    public void delete(Long reviewerId, Long reviewId) {
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다: " + reviewId));

        if (!r.getReviewerId().equals(reviewerId)) {
            throw new SecurityException("리뷰 삭제 권한이 없습니다.");
        }
        reviewRepository.delete(r);
    }

    @Transactional(readOnly = true)
    public Page<Review> listForReviewee(Long revieweeId, Pageable pageable) {
        return reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(revieweeId, pageable);
    }

    @Transactional(readOnly = true)
    public ReviewStatsResponse stats(Long revieweeId) {
        long count = reviewRepository.countByRevieweeId(revieweeId);
        double avg = reviewRepository.avgRatingByReviewee(revieweeId);

        var buckets = reviewRepository.ratingBuckets(revieweeId);
        Map<Integer, Long> histogram = new HashMap<>();
        for (int i = 1; i <= 5; i++) histogram.put(i, 0L);
        for (Object[] row : buckets) {
            Integer rating = (Integer) row[0];
            Long cnt = (Long) row[1];
            histogram.put(rating, cnt);
        }

        return new ReviewStatsResponse(count, avg, histogram);
    }

    // 유틸: username → 사용자 ID
    @Transactional(readOnly = true)
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
    }
}
