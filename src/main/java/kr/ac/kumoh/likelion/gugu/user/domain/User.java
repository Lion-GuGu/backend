package kr.ac.kumoh.likelion.gugu.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=50)
    private String username;

    @Column(nullable=false, length=255)
    private String password;

    @Column(nullable=false, length=60)
    private String name;

    @Column(nullable=false, unique=true, length=120)
    private String email;

    @Column(updatable = false, insertable = false)
    private java.sql.Timestamp createdAt;
    @Column(insertable = false)
    private java.sql.Timestamp updatedAt;
}