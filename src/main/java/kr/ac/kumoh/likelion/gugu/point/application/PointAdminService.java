package kr.ac.kumoh.likelion.gugu.point.application;

import kr.ac.kumoh.likelion.gugu.dto.PageResponse;
import kr.ac.kumoh.likelion.gugu.point.domain.PointWallet;
import kr.ac.kumoh.likelion.gugu.point.domain.ReasonCode;
import kr.ac.kumoh.likelion.gugu.point.infra.PointRepository;
import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import kr.ac.kumoh.likelion.gugu.point.web.dto.PointDtos.AdminBalanceItem;
import kr.ac.kumoh.likelion.gugu.point.web.dto.PointDtos.AdminBalanceOneRes;

@Service
@RequiredArgsConstructor
public class PointAdminService {

    private final PointService pointService;
    private final PointRepository pointRepo;
    private final UserRepository userRepo;       // username 채우려면 필요

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

    /** NEW: 전체 잔액 목록 (page/size) */
    @Transactional(readOnly = true)
    public PageResponse<AdminBalanceItem> getAllBalances(Pageable pageable) {
        Page<PointWallet> page = pointRepo.findAll(pageable); // JpaRepository 기본 제공
        return PageResponse.of(page, this::toItem);
    }

    /** NEW: 특정 유저 잔액 단건 */
    @Transactional(readOnly = true)
    public AdminBalanceOneRes getBalance(Long userId) {
        PointWallet w = pointRepo.findById(userId).orElse(null); // 없으면 0원 취급
        String username = userRepo.findById(userId).map(u -> u.getUsername()).orElse(null);

        if (w == null) {
            return new AdminBalanceOneRes(userId, username, 0L, null);
        }
        return new AdminBalanceOneRes(w.getUserId(), username, w.getBalance(), w.getUpdatedAt());
    }

    private AdminBalanceItem toItem(PointWallet w) {
        String username = userRepo.findById(w.getUserId()).map(u -> u.getUsername()).orElse(null);
        return new AdminBalanceItem(w.getUserId(), username, w.getBalance(), w.getUpdatedAt());
    }
}