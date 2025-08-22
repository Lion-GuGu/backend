package kr.ac.kumoh.likelion.gugu.auth;

import kr.ac.kumoh.likelion.gugu.user.application.UserService;
import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository;
import jakarta.validation.Valid;
import kr.ac.kumoh.likelion.gugu.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // 회원가입용
    private final UserService userService;

    // 로그인용 (기존 AuthSignupController에서 가져옴)
    private final AuthenticationManager authManager;
    private final JwtEncoder jwtEncoder;

    // --- 요청/응답에 사용할 데이터 형식 정의 ---
    public record LoginRequest(String username, String password) {}
    public record TokenResponse(String access_token, long expires_in) {}

    /**
     * 회원가입을 처리하는 엔드포인트
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Long>> signUp(@Valid @RequestBody SignUpRequest req) {
        Long id = userService.signUp(req);
        return ResponseEntity.ok(Map.of("id", id));
    }

    /**
     * 로그인을 처리하고 JWT를 발급하는 엔드포인트
     * (이 부분이 누락되어 있었습니다)
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest req) {
        // 1. 사용자 인증 요청 객체 생성
        Authentication authenticationRequest =
                new UsernamePasswordAuthenticationToken(req.username(), req.password());

        // 2. 인증 수행
        Authentication authenticationResponse = this.authManager.authenticate(authenticationRequest);

        // 3. 인증된 사용자의 ID를 DB에서 조회
        String username = authenticationResponse.getName();
        User user = UserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        Long userId = user.getId();

        // 4. JWT 생성
        Instant now = Instant.now();
        long expiresIn = 3600L; // 토큰 만료 시간: 1시간

        String scope = authenticationResponse.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(username)
                .claim("scope", scope)
                .claim("userId", userId) // <-- 핵심: userId를 토큰에 추가
                .build();

        // 5. JWT 인코딩 및 토큰 생성
        var encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        String token = this.jwtEncoder.encode(encoderParameters).getTokenValue();

        // 6. 생성된 토큰을 응답으로 반환
        return ResponseEntity.ok(new TokenResponse(token, expiresIn));
    }
}