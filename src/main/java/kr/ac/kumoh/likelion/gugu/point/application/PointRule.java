package kr.ac.kumoh.likelion.gugu.point.application;

import kr.ac.kumoh.likelion.gugu.point.domain.PointTransaction;
import kr.ac.kumoh.likelion.gugu.point.domain.ReasonCode;

public enum PointRule {
    // ====== EARN 규칙 ======
    ANSWER_ACCEPTED(PointTransaction.TxType.EARN, 50, ReasonCode.ANSWER_ACCEPTED),
    POST_WRITE(PointTransaction.TxType.EARN, 5, ReasonCode.POST_WRITE),
    COMMENT_WRITE(PointTransaction.TxType.EARN, 2, ReasonCode.COMMENT_WRITE),

    // ====== SPEND 규칙 ======
    SHOP_PURCHASE(PointTransaction.TxType.SPEND, 0, ReasonCode.SHOP_PURCHASE); // 금액은 런타임 입력

    public final PointTransaction.TxType txType;
    public final long defaultAmount; // SPEND는 호출 시 override 가능
    public final ReasonCode reason;

    PointRule(PointTransaction.TxType txType, long defaultAmount, ReasonCode reason) {
        this.txType = txType;
        this.defaultAmount = defaultAmount;
        this.reason = reason;
    }

    /** 규칙에 정의된 기본 포인트(부호 포함) 반환 */
    public long signedDefaultAmount() {
        return txType == PointTransaction.TxType.SPEND ? -Math.abs(defaultAmount) : Math.abs(defaultAmount);
    }
}

