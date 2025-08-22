package kr.ac.kumoh.likelion.gugu.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignUpRequest {
    @NotBlank private String username;   // 아이디
    @NotBlank private String password;   // 평문으로 전달 → 서버에서 해시
    @NotBlank private String name;
    @Email @NotBlank private String email;
}