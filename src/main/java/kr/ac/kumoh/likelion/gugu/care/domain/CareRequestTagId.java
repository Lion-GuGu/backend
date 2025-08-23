package kr.ac.kumoh.likelion.gugu.care.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class CareRequestTagId implements Serializable {
    private Long requestId;
    private Long tagId;
}