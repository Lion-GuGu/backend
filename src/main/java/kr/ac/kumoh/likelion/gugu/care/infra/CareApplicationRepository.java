package kr.ac.kumoh.likelion.gugu.care.infra;

import kr.ac.kumoh.likelion.gugu.care.domain.CareApplication;
import kr.ac.kumoh.likelion.gugu.care.domain.type.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CareApplicationRepository extends JpaRepository<CareApplication, Long> {
    boolean existsByRequestIdAndApplicantId(Long requestId, Long applicantId);

    @Query("""
        select (count(a) > 0)
        from CareApplication a
        join a.request r
        where a.applicant.id = :applicantId
          and a.status = kr.ac.kumoh.likelion.gugu.care.domain.type.ApplicationStatus.PENDING
          and r.dateOnly = :dateOnly
          and r.startTime < :endTime
          and r.endTime   > :startTime
    """)
    boolean existsOverlappingPendingApplication(
            @Param("applicantId") Long applicantId,
            @Param("dateOnly") LocalDate dateOnly,
            @Param("startTime") LocalTime startTime,
            @Param("endTime")     LocalTime endTime
    );
    Optional<CareApplication> findByRequestIdAndApplicantId(Long requestId, Long applicantId);

    List<CareApplication> findByRequestIdOrderByIdDesc(Long requestId);
    long countByRequestId(Long requestId);

    List<CareApplication> findByRequestIdAndStatus(Long requestId, ApplicationStatus status);
}
