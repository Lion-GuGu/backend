package kr.ac.kumoh.likelion.gugu.domain.community.event;

import kr.ac.kumoh.likelion.gugu.domain.point.PointRule;
import kr.ac.kumoh.likelion.gugu.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityPointListener {

    private final PointService pointService;

    @EventListener
    public void onCommentCreated(CommentCreatedEvent e) {
        // 자답 제외를 원하면 주석 해제
        // if (e.commentAuthorId().equals(e.postAuthorId())) return;

        pointService.earnByRule(
                e.commentAuthorId(),
                PointRule.COMMENT_WRITE,      // ← 규칙: 댓글 작성 시 N점
                "POST",
                e.postId()
        );
    }
}