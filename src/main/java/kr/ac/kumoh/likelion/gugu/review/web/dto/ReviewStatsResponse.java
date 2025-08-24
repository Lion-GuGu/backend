package kr.ac.kumoh.likelion.gugu.review.web.dto;

import java.util.Map;

public record ReviewStatsResponse(
        long count,
        double average,
        Map<Integer, Long> histogram
) {}
