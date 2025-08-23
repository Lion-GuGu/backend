package kr.ac.kumoh.likelion.gugu.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignUpRequest {
    @NotBlank private String username;   // 아이디
    @NotBlank private String password;   // 평문으로 전달 → 서버에서 해시
    @NotBlank private String name;
    @Email @NotBlank private String email;

    // 아이 정보 추가
    @NotNull
    private Integer childAge;              // 아이 나이
    @NotBlank private String childGender;            // 아이 성별 (예: "남", "여")
    @NotBlank private String childSchool;            // 아이 학교
    @NotBlank private String childResidence;         // 아이 거주지
}