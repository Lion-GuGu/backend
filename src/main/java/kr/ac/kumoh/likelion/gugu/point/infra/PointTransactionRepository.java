package kr.ac.kumoh.likelion.gugu.point.infra;

import kr.ac.kumoh.likelion.gugu.point.domain.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    // userId가 주어지면 해당 유저의 거래 내역, 없으면 전체 거래 내역을 조회
    @Query("SELECT pt FROM PointTransaction pt WHERE (:userId IS NULL OR pt.userId = :userId) ORDER BY pt.createdAt DESC")
    Page<PointTransaction> findByUserIdOrAllTransactions(
            @Param("userId") Long userId,
            Pageable pageable
    );
}