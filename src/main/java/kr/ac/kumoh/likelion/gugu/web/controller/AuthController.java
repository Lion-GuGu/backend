package kr.ac.kumoh.likelion.gugu.web.controller;

import kr.ac.kumoh.likelion.gugu.service.UserService;
import kr.ac.kumoh.likelion.gugu.web.dto.user.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Long>> signUp(@Valid @RequestBody SignUpRequest req) {
        Long id = userService.signUp(req);
        return ResponseEntity.ok(Map.of("id", id));
    }
}