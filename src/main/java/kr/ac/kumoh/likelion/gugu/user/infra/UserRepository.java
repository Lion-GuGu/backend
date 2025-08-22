package kr.ac.kumoh.likelion.gugu.user.infra;

import kr.ac.kumoh.likelion.gugu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    static Optional<User> findByUsername(String username);
}