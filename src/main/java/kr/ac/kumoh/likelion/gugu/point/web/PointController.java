package kr.ac.kumoh.likelion.gugu.point.web;

import kr.ac.kumoh.likelion.gugu.point.web.dto.PointDtos.*;
import kr.ac.kumoh.likelion.gugu.point.application.PointRule;
import kr.ac.kumoh.likelion.gugu.point.domain.ReasonCode;
import kr.ac.kumoh.likelion.gugu.user.domain.User;
import kr.ac.kumoh.likelion.gugu.point.application.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/me")
    public BalanceRes myBalance(@AuthenticationPrincipal Jwt jwt) {
        Number uid = jwt.getClaim("userId"); // <- Long/Integer 어떤 경우든 안전
        long userId = uid.longValue();
        return new BalanceRes(pointService.getBalance(userId));
    }

    @GetMapping("/me/transactions")
    public PageRes<TxRes> myTx(
            @AuthenticationPrincipal(expression = "claims['userId']") Number uid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var p = pointService.history(uid.longValue(), PageRequest.of(page, size));
        return new PageRes<>(
                p.getContent().stream().map(TxRes::from).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements()
        );
    }

    @PostMapping("/earn/by-rule")
    public BalanceRes earnByRule(@AuthenticationPrincipal User me,
                                 @RequestBody EarnByRuleReq req) {
        PointRule rule = PointRule.valueOf(req.rule());
        long balance = pointService.earnByRule(me.getId(), rule, req.refType(), req.refId());
        return new BalanceRes(balance);
    }

    @PostMapping("/earn")
    public BalanceRes earn(@AuthenticationPrincipal User me,
                           @RequestBody EarnReq req) {
        long balance = pointService.earn(
                me.getId(), req.amount(),
                ReasonCode.valueOf(req.reasonCode()),
                req.refType(), req.refId()
        );
        return new BalanceRes(balance);
    }

    @PostMapping("/spend")
    public BalanceRes spend(@AuthenticationPrincipal User me,
                            @RequestBody SpendReq req) {
        long balance = pointService.spend(
                me.getId(), req.amount(),
                ReasonCode.valueOf(req.reasonCode()),
                req.refType(), req.refId()
        );
        return new BalanceRes(balance);
    }
}