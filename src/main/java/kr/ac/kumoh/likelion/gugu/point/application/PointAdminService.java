package kr.ac.kumoh.likelion.gugu.point.application;

import kr.ac.kumoh.likelion.gugu.point.domain.ReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointAdminService {

    private final PointService pointService;

    /** 운영자 수동 가감 (로그 남기기 위해 ADJUST 사유 사용 권장) */
    @Transactional
    public long adjust(Long userId, long delta, String memo) {
        if (delta == 0) return pointService.getBalance(userId);

        if (delta > 0) {
            return pointService.earn(userId, delta, ReasonCode.ADJUST_PLUS, "ADMIN", null);
        } else {
            return pointService.spend(userId, -delta, ReasonCode.ADJUST_MINUS, "ADMIN", null);
        }
    }
}