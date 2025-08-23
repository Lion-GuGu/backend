package kr.ac.kumoh.likelion.gugu.care.infra;

import kr.ac.kumoh.likelion.gugu.care.domain.CareMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareMatchRepository extends JpaRepository<CareMatch, Long> {
}