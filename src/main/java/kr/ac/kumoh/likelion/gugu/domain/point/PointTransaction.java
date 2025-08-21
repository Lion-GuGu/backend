package kr.ac.kumoh.likelion.gugu.domain.point;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "point_transaction")
public class PointTransaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    /** +면 적립, -면 차감 */
    @Column(nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TxType type;                 // EARN, SPEND, ADJUST

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private ReasonCode reasonCode;       // 규칙/행동 코드

    @Column(length = 64)
    private String refType;              // 연관 리소스 타입(POST, ORDER 등)

    private Long refId;                  // 연관 리소스 id

    @CreationTimestamp
    private Instant createdAt;

    public static PointTransaction of(Long userId, long amount, TxType type,
                                      ReasonCode reason, String refType, Long refId) {
        return PointTransaction.builder()
                .userId(userId)
                .amount(amount)
                .type(type)
                .reasonCode(reason)
                .refType(refType)
                .refId(refId)
                .build();
    }

    public enum TxType { EARN, SPEND, ADJUST }
}