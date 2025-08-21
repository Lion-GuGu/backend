package kr.ac.kumoh.likelion.gugu.domain.point;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface PointRepository extends JpaRepository<PointWallet, Long> {

    /** 지갑 행을 비관적 락으로 가져옴(경합 높은 구간에서 사용) */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from PointWallet w where w.userId = :userId")
    Optional<PointWallet> findByIdForUpdate(@Param("userId") Long userId);
}