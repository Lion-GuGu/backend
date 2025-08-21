package kr.ac.kumoh.likelion.gugu.domain.point;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "point_wallet")
public class PointWallet {

    @Id
    private Long userId;                 // user.id 1:1

    @Version
    private Long version;                // 낙관적 락 (동시성 제어)

    @Column(nullable = false)
    private long balance = 0L;

    @UpdateTimestamp
    private Instant updatedAt;

    public PointWallet(Long userId) {
        this.userId = userId;
        this.balance = 0L;
    }

    /** +적립 / -차감, 잔액 음수 방지 */
    public void add(long delta) {
        long next = this.balance + delta;
        if (next < 0) {
            throw new IllegalStateException("포인트 부족");
        }
        this.balance = next;
    }
}