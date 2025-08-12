package kr.ac.kumoh.likelion.gugu.service;

import kr.ac.kumoh.likelion.gugu.domain.user.User;
import kr.ac.kumoh.likelion.gugu.domain.user.UserRepository;
import kr.ac.kumoh.likelion.gugu.web.dto.user.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(SignUpRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");

        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword())); // BCrypt 해시
        u.setName(req.getName());
        u.setEmail(req.getEmail());

        return userRepository.save(u).getId();
    }
}