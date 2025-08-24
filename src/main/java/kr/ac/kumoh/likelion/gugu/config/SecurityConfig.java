package kr.ac.kumoh.likelion.gugu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecretBase64;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/**",
                                "/", "/test.html", "/favicon.ico",
                                "/static/**", "/assets/**", "/css/**", "/js/**", "/images/**",
                                "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()

                        // 내 잔액/내 거래내역: 로그인 필요
                        .requestMatchers(HttpMethod.GET, "/api/points/me", "/api/points/me/**").authenticated()

                        // ✅ 전체/특정 잔액: 로그인 필요 (역할 검사 없음)
                        .requestMatchers(HttpMethod.GET, "/api/points", "/api/points/*").authenticated()

                        // ✅ (원하면) 거래내역 전체/특정도 로그인만 필요하게
                        .requestMatchers(HttpMethod.GET, "/api/point-transactions", "/api/point-transactions/**").authenticated()

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
    }

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds, PasswordEncoder pe) {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(pe);
        return new ProviderManager(List.of(provider));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secret = Base64.getDecoder().decode(jwtSecretBase64);
        var key = new SecretKeySpec(secret, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        byte[] secret = Base64.getDecoder().decode(jwtSecretBase64);
        var jwk = new com.nimbusds.jose.jwk.OctetSequenceKey.Builder(secret).build();
        var src = (com.nimbusds.jose.jwk.source.JWKSource<com.nimbusds.jose.proc.SecurityContext>)
                (selector, ctx) -> List.of(jwk);
        return new NimbusJwtEncoder(src);
    }
}
