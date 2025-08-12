package kr.ac.kumoh.likelion.gugu.domain.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import kr.ac.kumoh.likelion.gugu.domain.user.User;

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
    @Column(nullable=false, length=10)
    private RequestStatus status = RequestStatus.OPEN;
}