package kr.ac.kumoh.likelion.gugu.domain.request;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class CareRequestTagId implements java.io.Serializable {
    private Long requestId;
    private Long tagId;
}

@Entity
@Getter @Setter
@Table(name="care_request_tag")
public class CareRequestTag {
    @EmbeddedId
    private CareRequestTagId id;

    @ManyToOne(fetch= FetchType.LAZY) @MapsId("requestId")
    @JoinColumn(name="request_id")
    private CareRequest request;

    @ManyToOne(fetch=FetchType.LAZY) @MapsId("tagId")
    @JoinColumn(name="tag_id")
    private Tag tag;
}