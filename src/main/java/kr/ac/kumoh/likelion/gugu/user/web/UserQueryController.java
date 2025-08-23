package kr.ac.kumoh.likelion.gugu.user.web;

import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserQueryController {
    private final UserRepository userRepository;

    @GetMapping("/users")
    public List<Map<String, Object>> findAll() {
        return userRepository.findAll().stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("name", u.getName());
            m.put("email", u.getEmail());
            return m;
        }).toList(); // JDK 16 이상
    }

    @GetMapping("/users/{id}")
    public Map<String, Object> findOne(@PathVariable Long id) {
        var u = userRepository.findById(id).orElseThrow();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("username", u.getUsername());
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        return m;
    }
}
