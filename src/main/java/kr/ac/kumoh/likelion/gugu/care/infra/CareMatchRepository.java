package kr.ac.kumoh.likelion.gugu.care.infra;

import kr.ac.kumoh.likelion.gugu.care.domain.CareMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.ac.kumoh.likelion.gugu.care.domain.CareMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;

public interface CareMatchRepository extends JpaRepository<CareMatch, Long> {

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
            @Param("dateOnly")   LocalDate dateOnly,
            @Param("startTime")  LocalTime startTime,
            @Param("endTime")    LocalTime endTime
    );
}