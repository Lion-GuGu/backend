package kr.ac.kumoh.likelion.gugu.review.web.dto;

import kr.ac.kumoh.likelion.gugu.review.domain.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long reviewerId,
        Long revieweeId,
        int rating,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getReviewerId(),
                review.getRevieweeId(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
