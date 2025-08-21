package kr.ac.kumoh.likelion.gugu.domain.community.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;          // 참조 게시글

    @Column(nullable = false)
    private Long authorId;        // 댓글 작성자

    private Long parentId;        // 대댓글(선택)

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean deleted = false;

    @CreationTimestamp
    private Instant createdAt;
}