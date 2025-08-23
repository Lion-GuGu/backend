package kr.ac.kumoh.likelion.gugu.point.web;

import kr.ac.kumoh.likelion.gugu.point.application.PointTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import kr.ac.kumoh.likelion.gugu.point.domain.PointTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/point-transactions")
public class PointTransactionController {

    private final PointTransactionService pointTransactionService;

    @Autowired
    public PointTransactionController(PointTransactionService pointTransactionService) {
        this.pointTransactionService = pointTransactionService;
    }

    // userId가 없으면 모든 거래 내역, 있으면 해당 유저의 거래 내역 조회
    @GetMapping
    public Page<PointTransaction> getPointTransactions(
            @RequestParam(required = false) Long userId,  // userId가 없을 수도 있기 때문에 required = false
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return pointTransactionService.getPointTransactions(userId, page, size);
    }
}