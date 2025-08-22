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
public class User implements UserDetails { // 'UserDetails'를 구현(implements)합니다.

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

    @Column(updatable = false, insertable = false)
    private java.sql.Timestamp createdAt;

    @Column(insertable = false)
    private java.sql.Timestamp updatedAt;


    // ---------- UserDetails 구현을 위한 메서드들 ----------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 모든 사용자에게 'ROLE_USER' 권한을 부여합니다.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // getPassword()와 getUsername()은 Lombok의 @Getter가 자동으로 생성해 줍니다.

    @Override
    public boolean isAccountNonExpired() {
        return true; // true: 계정이 만료되지 않았음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // true: 계정이 잠기지 않았음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true: 비밀번호가 만료되지 않았음
    }

    @Override
    public boolean isEnabled() {
        return true; // true: 계정이 활성화되었음
    }
}