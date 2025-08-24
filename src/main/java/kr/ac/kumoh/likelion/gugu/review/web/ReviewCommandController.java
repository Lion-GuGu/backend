package kr.ac.kumoh.likelion.gugu.review.web;

import jakarta.validation.Valid;
import kr.ac.kumoh.likelion.gugu.review.application.ReviewService;
import kr.ac.kumoh.likelion.gugu.review.web.dto.CreateReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User; // 스프링 시큐리티 User
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewCommandController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateReviewDto dto
    ) {
        if (jwt == null) return ResponseEntity.status(401).body("인증 필요");

        // 기본값: principal name == sub
        String username = jwt.getSubject(); // 또는 jwt.getClaim("username")
        Long meId = reviewService.getUserIdByUsername(username);

        Long id = reviewService.create(meId, dto.revieweeId(), dto.rating(), dto.content());
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long reviewId
    ) {
        if (jwt == null) return ResponseEntity.status(401).body("인증 필요");

        String username = jwt.getSubject();
        Long meId = reviewService.getUserIdByUsername(username);

        reviewService.delete(meId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
