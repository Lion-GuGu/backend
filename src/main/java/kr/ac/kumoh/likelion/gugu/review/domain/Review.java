package kr.ac.kumoh.likelion.gugu.review.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                // 같은 리뷰어가 같은 리뷰이에게는 1번만 작성 가능 (필요 없으면 제거해도 됨)
                @UniqueConstraint(name = "uk_reviewer_reviewee", columnNames = {"reviewer_id", "reviewee_id"})
        },
        indexes = {
                @Index(name = "idx_review_reviewee", columnList = "reviewee_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자(로그인 사용자)
    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    // 대상 사용자
    @Column(name = "reviewee_id", nullable = false)
    private Long revieweeId;

    // 별점 1~5 (업데이트 API는 사용하지 않더라도 생성 시 검증은 유지)
    @Min(1) @Max(5)
    @Column(nullable = false)
    private int rating;

    // 리뷰 내용 (선택)
    @Column(length = 2000)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
