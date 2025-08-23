package kr.ac.kumoh.likelion.gugu.point.application;

import kr.ac.kumoh.likelion.gugu.point.domain.PointTransaction;
import kr.ac.kumoh.likelion.gugu.point.infra.PointTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PointTransactionService {

    private final PointTransactionRepository pointTransactionRepository;

    @Autowired
    public PointTransactionService(PointTransactionRepository pointTransactionRepository) {
        this.pointTransactionRepository = pointTransactionRepository;
    }

    // userId가 없으면 모든 거래 내역, 있으면 해당 유저의 거래 내역 조회
    public Page<PointTransaction> getPointTransactions(Long userId, int page, int size) {
        return pointTransactionRepository.findByUserIdOrAllTransactions(userId, PageRequest.of(page, size));
    }
}
