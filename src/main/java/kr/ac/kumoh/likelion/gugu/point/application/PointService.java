package kr.ac.kumoh.likelion.gugu.point.application;

import kr.ac.kumoh.likelion.gugu.point.domain.PointTransaction;
import kr.ac.kumoh.likelion.gugu.point.domain.PointWallet;
import kr.ac.kumoh.likelion.gugu.point.domain.ReasonCode;
import kr.ac.kumoh.likelion.gugu.point.infra.PointRepository;
import kr.ac.kumoh.likelion.gugu.point.infra.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository walletRepo;
    private final PointTransactionRepository txRepo;

    /** 규칙 기반 적립 (기본 포인트 사용) */
    @Transactional
    public long earnByRule(Long userId, PointRule rule, String refType, Long refId) {
        long delta = Math.abs(rule.signedDefaultAmount()); // 항상 + 로 사용
        return apply(userId, delta, PointTransaction.TxType.EARN, rule.reason, refType, refId);
    }

    /** 금액 지정 적립 */
    @Transactional
    public long earn(Long userId, long amount, ReasonCode reason, String refType, Long refId) {
        if (amount <= 0) throw new IllegalArgumentException("적립 금액은 양수여야 합니다.");
        return apply(userId, amount, PointTransaction.TxType.EARN, reason, refType, refId);
    }

    /** 금액 지정 차감 */
    @Transactional
    public long spend(Long userId, long amount, ReasonCode reason, String refType, Long refId) {
        if (amount <= 0) throw new IllegalArgumentException("차감 금액은 양수여야 합니다.");
        return apply(userId, -amount, PointTransaction.TxType.SPEND, reason, refType, refId);
    }

    /** 공통 처리: 지갑 증감 + 이력 기록 (원자적) */
    private long apply(Long userId, long signedDelta, PointTransaction.TxType type,
                       ReasonCode reason, String refType, Long refId) {

        // 존재하지 않으면 지갑 생성
        PointWallet wallet = walletRepo.findByIdForUpdate(userId)
                .orElseGet(() -> walletRepo.save(new PointWallet(userId)));

        wallet.add(signedDelta); // 잔액 검증 포함
        txRepo.save(PointTransaction.of(userId, signedDelta, type, reason, refType, refId));
        return wallet.getBalance();
    }

    // 조회
    @Transactional(readOnly = true)
    public long getBalance(Long userId) {
        return walletRepo.findById(userId).map(PointWallet::getBalance).orElse(0L);
    }

    @Transactional(readOnly = true)
    public Page<PointTransaction> history(Long userId, Pageable pageable) {
        return txRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}