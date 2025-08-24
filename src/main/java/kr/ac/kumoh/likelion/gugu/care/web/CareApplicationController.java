package kr.ac.kumoh.likelion.gugu.care.web;

import kr.ac.kumoh.likelion.gugu.care.application.CareApplicationService;
import kr.ac.kumoh.likelion.gugu.care.domain.CareApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/requests/{requestId}/applications")
@RequiredArgsConstructor
public class CareApplicationController {

    private final CareApplicationService service;

    public record IdResponse(Long id) {}
    public record ApplicantItem(Long userId, String username, String status, LocalDateTime appliedAt) {}

    @PostMapping
    public ResponseEntity<IdResponse> apply(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = extractUserId(jwt);
        Long id = service.apply(userId, requestId);
        // 생성 결과는 201 Created + Location 헤더 권장
        return ResponseEntity.created(URI.create("/api/requests/" + requestId + "/applications/" + id))
                .body(new IdResponse(id));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = extractUserId(jwt);
        service.withdraw(userId, requestId);
        return ResponseEntity.noContent().build();
    }

    // 요청자 본인만 신청자 목록 조회
    @GetMapping
    public ResponseEntity<List<ApplicantItem>> list(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = extractUserId(jwt);
        List<CareApplication> apps = service.listApplicants(actorId, requestId);
        List<ApplicantItem> result = apps.stream().map(a ->
                new ApplicantItem(
                        a.getApplicant().getId(),
                        a.getApplicant().getUsername(),
                        a.getStatus().name(),
                        a.getCreatedAt()
                )
        ).toList();
        return ResponseEntity.ok(result);
    }

    private Long extractUserId(Jwt jwt) {
        Object claim = jwt.getClaims().get("userId");
        if (claim instanceof Number num) return num.longValue();
        if (claim instanceof String s)   return Long.parseLong(s);
        throw new IllegalArgumentException("JWT에 userId 클레임이 없습니다.");
    }
}
