package kr.ac.kumoh.likelion.gugu.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    // 아이 정보 매핑 (snake_case 컬럼명 지정)
    @Column(name = "child_age")
    private Integer childAge;      // 아이 나이

    @Column(name = "child_gender")
    private String childGender;    // 아이 성별

    @Column(name = "child_school")
    private String childSchool;    // 아이 학교

    @Column(name = "child_residence")
    private String childResidence; // 아이 거주지

    public void setChildInfo(Integer childAge, String childGender, String childSchool, String childResidence) {
        this.childAge = childAge;
        this.childGender = childGender;
        this.childSchool = childSchool;
        this.childResidence = childResidence;
    }

    @Column(name = "created_at", updatable = false, insertable = false)
    private java.sql.Timestamp createdAt;

    @Column(name = "updated_at", insertable = false)
    private java.sql.Timestamp updatedAt;

    // ---------- UserDetails 구현 ----------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
