package kr.ac.kumoh.likelion.gugu.domain.request;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import kr.ac.kumoh.likelion.gugu.domain.tag.Tag;

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