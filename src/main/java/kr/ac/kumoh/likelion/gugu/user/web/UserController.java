package kr.ac.kumoh.likelion.gugu.user.web;

import kr.ac.kumoh.likelion.gugu.user.domain.User;
import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // 단일 사용자 조회
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .childAge(user.getChildAge())
                .childGender(user.getChildGender())
                .childSchool(user.getChildSchool())
                .childResidence(user.getChildResidence())
                .build();
    }

    // 모든 사용자 조회
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .email(user.getEmail())
                        .childAge(user.getChildAge())
                        .childGender(user.getChildGender())
                        .childSchool(user.getChildSchool())
                        .childResidence(user.getChildResidence())
                        .build())
                .toList();
    }
}
