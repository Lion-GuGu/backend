package kr.ac.kumoh.likelion.gugu.care.domain;

import jakarta.persistence.*;
import kr.ac.kumoh.likelion.gugu.care.domain.type.Category;
import kr.ac.kumoh.likelion.gugu.care.domain.type.ChildGender;
import kr.ac.kumoh.likelion.gugu.care.domain.type.RequestStatus;
import lombok.Getter;
import lombok.Setter;
import kr.ac.kumoh.likelion.gugu.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class CareRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="parent_id", nullable=false)
    private User parent;

    @Column(nullable=false, length=120)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private Category category; // 긴급/돌봄/교육/기타

    @Column(name = "date_only")
    private LocalDate dateOnly;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(nullable=false, insertable=false, updatable=false)
    private java.sql.Timestamp startAt; // generated column
    @Column(nullable=false, insertable=false, updatable=false)
    private java.sql.Timestamp endAt;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=6)
    private ChildGender childGender; // MALE/FEMALE

    @Column(nullable=false)
    private Integer childAge;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private RequestStatus status = RequestStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_provider_id")
    private User matchedProvider;

    private java.time.LocalDateTime matchedAt;

    @PrePersist
    void prePersist() {
        if (status == null) status = RequestStatus.OPEN;
    }
}