package kr.ac.kumoh.likelion.gugu.care.domain;

import jakarta.persistence.*;
import kr.ac.kumoh.likelion.gugu.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class CareMatch {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private CareRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider; // 돌봄을 '수행한' 사용자

    @CreationTimestamp
    private Instant matchedAt; // 매칭된 시각

    public CareMatch(CareRequest request, User provider) {
        this.request = request;
        this.provider = provider;
    }
}