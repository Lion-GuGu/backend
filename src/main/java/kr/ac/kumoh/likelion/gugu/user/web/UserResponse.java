package kr.ac.kumoh.likelion.gugu.user.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String email;

    // 아이 정보
    private Integer childAge;
    private String childGender;
    private String childSchool;
    private String childResidence;
}