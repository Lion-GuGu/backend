package kr.ac.kumoh.likelion.gugu.care.infra;

import kr.ac.kumoh.likelion.gugu.care.domain.CareMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.ac.kumoh.likelion.gugu.care.domain.CareMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CareMatchRepository extends JpaRepository<CareMatch, Long> {

    // 제공자(돌봄 수행자) 입장: 내 매칭 일정
    @Query("""
      select m from CareMatch m
      join fetch m.request r
      where m.provider.id = :userId
        and r.dateOnly between :from and :to
      order by r.dateOnly asc, r.startTime asc
    """)
    List<CareMatch> findForProviderBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // 부모(요청자) 입장: 내가 요청한 것이 매칭된 일정
    @Query("""
      select m from CareMatch m
      join fetch m.request r
      where r.parent.id = :userId
        and r.dateOnly between :from and :to
      order by r.dateOnly asc, r.startTime asc
    """)
    List<CareMatch> findForParentBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // 특정 제공자가 같은 날짜/시간대에 이미 매칭된 일정이 있는지 확인
    @Query("""
        select (count(m) > 0)
        from CareMatch m
        join m.request r
        where m.provider.id = :providerId
          and r.dateOnly = :dateOnly
          and r.startTime < :endTime
          and r.endTime   > :startTime
    """)
    boolean existsOverlappingMatch(
            @Param("providerId") Long providerId,
            @Param("dateOnly") LocalDate dateOnly,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    boolean existsByRequestId(Long requestId);        // ← 추가
    java.util.Optional<CareMatch> findByRequestId(Long requestId); // ← (멱등용) 선택
}