package kr.ac.kumoh.likelion.gugu.point.web;

import kr.ac.kumoh.likelion.gugu.dto.PageResponse;
import kr.ac.kumoh.likelion.gugu.point.application.PointAdminService;
import kr.ac.kumoh.likelion.gugu.point.web.dto.PointDtos.AdminBalanceItem;
import kr.ac.kumoh.likelion.gugu.point.web.dto.PointDtos.AdminBalanceOneRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointQueryController {

    private final PointAdminService pointAdminService;

    /** 전체 유저 잔액 조회 (로그인만 필요) */
    @GetMapping
    public PageResponse<AdminBalanceItem> getAll(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return pointAdminService.getAllBalances(PageRequest.of(page, size));
    }

    /** 특정 유저 잔액 조회 (로그인만 필요) */
    @GetMapping("/{userId}")
    public AdminBalanceOneRes getOne(@PathVariable Long userId) {
        return pointAdminService.getBalance(userId);
    }
}
