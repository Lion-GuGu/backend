package kr.ac.kumoh.likelion.gugu.auth;

import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthSignupController {

    private final AuthenticationManager authManager;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepo;

    public record LoginReq(String username, String password) {}
    public record TokenRes(String access_token, long expires_in) {}

    @PostMapping("/login")
    public ResponseEntity<TokenRes> login(@RequestBody LoginReq req) {
        // 아이디/비번 검증 (UserDetailsService + BCrypt)
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        // userId 클레임 채우기
        Long userId = userRepo.findByUsername(req.username()).orElseThrow().getId();

        // 액세스 토큰 생성
        Instant now = Instant.now();
        long expSeconds = 60L * 60L; // 60분
        var claims = JwtClaimsSet.builder()
                .subject(req.username())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expSeconds))
                .claim("userId", userId)
                .claim("roles", List.of("USER"))
                .build();
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return ResponseEntity.ok(new TokenRes(token, expSeconds));
    }
}
