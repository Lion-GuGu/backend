package kr.ac.kumoh.likelion.gugu.care.infra;

import kr.ac.kumoh.likelion.gugu.care.domain.CareRequestTag;
import kr.ac.kumoh.likelion.gugu.care.domain.CareRequestTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRequestTagRepository extends JpaRepository<CareRequestTag, CareRequestTagId> {
}