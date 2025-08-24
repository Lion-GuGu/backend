package kr.ac.kumoh.likelion.gugu.care.infra;

import kr.ac.kumoh.likelion.gugu.care.domain.CareApplication;
import kr.ac.kumoh.likelion.gugu.care.domain.type.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareApplicationRepository extends JpaRepository<CareApplication, Long> {
    boolean existsByRequestIdAndApplicantId(Long requestId, Long applicantId);
    Optional<CareApplication> findByRequestIdAndApplicantId(Long requestId, Long applicantId);

    List<CareApplication> findByRequestIdOrderByIdDesc(Long requestId);
    long countByRequestId(Long requestId);

    List<CareApplication> findByRequestIdAndStatus(Long requestId, ApplicationStatus status);
}
