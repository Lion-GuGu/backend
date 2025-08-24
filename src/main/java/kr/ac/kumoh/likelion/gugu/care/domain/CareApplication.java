package kr.ac.kumoh.likelion.gugu.care.domain;

import jakarta.persistence.*;
import kr.ac.kumoh.likelion.gugu.care.domain.type.ApplicationStatus;
import kr.ac.kumoh.likelion.gugu.care.domain.type.RequestStatus;
import kr.ac.kumoh.likelion.gugu.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "care_applications",
        uniqueConstraints = @UniqueConstraint(name = "uk_request_applicant", columnNames = {"request_id", "applicant_id"})
)
@Getter @Setter
@NoArgsConstructor
public class CareApplication {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신청 대상 돌봄 요청
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id")
    private CareRequest request;

    // 신청한 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id")
    private User applicant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING; // ✅ 타입/기본값 일치

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public CareApplication(CareRequest request, User applicant) {
        this.request = request;
        this.applicant = applicant;
    }
}
