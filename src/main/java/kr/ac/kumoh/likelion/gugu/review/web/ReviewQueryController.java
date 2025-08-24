package kr.ac.kumoh.likelion.gugu.review.web;

import kr.ac.kumoh.likelion.gugu.review.application.ReviewService;
import kr.ac.kumoh.likelion.gugu.review.web.dto.ReviewResponse;
import kr.ac.kumoh.likelion.gugu.review.web.dto.ReviewStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewQueryController {

    private final ReviewService reviewService;

    // 대상 사용자 리뷰 목록 (공개/또는 인증 정책에 따라)
    @GetMapping("/reviewee/{revieweeId}")
    public ResponseEntity<?> listByReviewee(
            @PathVariable Long revieweeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewResponse> result = reviewService
                .listForReviewee(revieweeId, pageable)
                .map(ReviewResponse::from);
        return ResponseEntity.ok(result);
    }

    // 대상 사용자 리뷰 통계
    @GetMapping("/reviewee/{revieweeId}/stats")
    public ResponseEntity<ReviewStatsResponse> stats(@PathVariable Long revieweeId) {
        return ResponseEntity.ok(reviewService.stats(revieweeId));
    }
}
