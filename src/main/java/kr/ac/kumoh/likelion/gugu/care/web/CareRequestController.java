package kr.ac.kumoh.likelion.gugu.care.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import kr.ac.kumoh.likelion.gugu.care.application.CareRequestService;
import kr.ac.kumoh.likelion.gugu.care.web.dto.request.CreateCareRequestDto;

import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class CareRequestController {

    private final CareRequestService service;

    // 둘 다 필요하므로 두 레코드 모두 유지
    public record IdResponse(Long id) {}
    public record MatchRequest(Long providerId) {}

    @PostMapping
    public ResponseEntity<IdResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateCareRequestDto dto
    ) {
        Long parentId = extractUserId(jwt);
        Long id = service.create(parentId, dto);
        return ResponseEntity.ok(new IdResponse(id));
    }

    /**
     * JWT에 담긴 userId 클레임에서 Long 값을 안전하게 추출
     * (String/Integer로 들어오는 경우도 처리)
     */
    private Long extractUserId(Jwt jwt) {
        Object claim = jwt.getClaims().get("userId");
        if (claim instanceof Number num) return num.longValue();
        if (claim instanceof String s)   return Long.parseLong(s);
        throw new IllegalArgumentException("JWT에 userId 클레임이 없습니다.");
    }

    @PostMapping("/{requestId}/match")
    public ResponseEntity<Map<String, Long>> match(
            @PathVariable Long requestId,
            @RequestBody MatchRequest req,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = extractUserId(jwt);
        Long matchId = service.matchProvider(actorId, requestId, req.providerId());
        return ResponseEntity.ok(Map.of("matchId", matchId));
    }
}
