package kr.ac.kumoh.likelion.gugu.community.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostCreatedEvent {
    private final Long authorId; // 글 작성자 ID
    private final Long postId;   // 글 ID(포인트 refId로 사용)
}
