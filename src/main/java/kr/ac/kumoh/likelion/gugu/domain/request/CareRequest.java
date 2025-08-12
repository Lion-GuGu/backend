package kr.ac.kumoh.likelion.gugu.domain.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(nullable=false) private java.sql.Date dateOnly;
    @Column(nullable=false) private java.sql.Time startTime;
    @Column(nullable=false) private java.sql.Time endTime;

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

public enum Category { 긴급, 돌봄, 교육, 기타 }
public enum ChildGender { MALE, FEMALE }
public enum RequestStatus { OPEN, MATCHED, CANCELLED, DONE }