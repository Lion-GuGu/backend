package kr.ac.kumoh.likelion.gugu.care.web;

import kr.ac.kumoh.likelion.gugu.care.application.CareRequestService;
import kr.ac.kumoh.likelion.gugu.care.web.dto.request.CreateCareRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class CareRequestController {

    private final CareRequestService service;

    public record MatchRequest(Long providerId) {}

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(
            @RequestParam Long parentId,
            @Valid @RequestBody CreateCareRequestDto dto) {
        Long id = service.create(parentId, dto);
        return ResponseEntity.ok(Map.of("id", id));
    }

    @PostMapping("/{requestId}/match")
    public ResponseEntity<Map<String, Long>> match(
            @PathVariable Long requestId,
            @RequestBody MatchRequest req,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = jwt.getClaim("userId"); // JWT 토큰에서 현재 로그인한 사용자 ID를 가져옴
        Long matchId = service.matchProvider(actorId, requestId, req.providerId());
        return ResponseEntity.ok(Map.of("matchId", matchId));
    }
}