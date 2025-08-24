package kr.ac.kumoh.likelion.gugu.review.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewDto(
        @NotNull Long revieweeId,
        @Min(1) @Max(5) int rating,
        @Size(max = 2000) String content
) {}
