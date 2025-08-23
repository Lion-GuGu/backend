package kr.ac.kumoh.likelion.gugu.community.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnswerAcceptedEvent {
    private final Long postId;          // 질문 글 ID
    private final Long commentId;       // 채택된 댓글(답변) ID
    private final Long questionerId;    // 질문 작성자 ID
    private final Long answererId;      // 답변(댓글) 작성자 ID
}
