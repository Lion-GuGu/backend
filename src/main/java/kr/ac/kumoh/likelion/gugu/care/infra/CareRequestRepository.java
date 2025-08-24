package kr.ac.kumoh.likelion.gugu.care.infra;

import kr.ac.kumoh.likelion.gugu.care.domain.CareRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface CareRequestRepository extends JpaRepository<CareRequest, Long> {

    // ✅ 비관락: 동시에 두 명 수락되는 것 방지
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from CareRequest r where r.id = :id")
    Optional<CareRequest> findByIdForUpdate(@Param("id") Long id);
}