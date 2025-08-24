package kr.ac.kumoh.likelion.gugu.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/** 공용 페이지 래퍼 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    public static <T, R> PageResponse<R> of(Page<T> page, Function<T, R> mapper) {
        return new PageResponse<>(
                page.map(mapper).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}