package kr.ac.kumoh.likelion.gugu.care.domain;

import jakarta.persistence.*;
import kr.ac.kumoh.likelion.gugu.care.domain.CareRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import kr.ac.kumoh.likelion.gugu.user.domain.User;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_care_match_request", columnNames = "request_id")
        }
)
public class CareMatch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private CareRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @CreationTimestamp
    private Instant matchedAt;

    public CareMatch(CareRequest request, User provider) {
        this.request = request;
        this.provider = provider;
    }
}
