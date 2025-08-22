package kr.ac.kumoh.likelion.gugu.care.infra;

import kr.ac.kumoh.likelion.gugu.care.domain.CareRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRequestRepository extends JpaRepository<CareRequest, Long> {
}