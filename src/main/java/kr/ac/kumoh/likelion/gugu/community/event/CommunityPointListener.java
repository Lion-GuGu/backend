package kr.ac.kumoh.likelion.gugu.community.event;

import kr.ac.kumoh.likelion.gugu.point.application.PointRule;
import kr.ac.kumoh.likelion.gugu.point.application.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class CommunityPointListener {

    private final PointService pointService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentCreated(CommentCreatedEvent e) {
        try {
            // 자답 제외 옵션
            if (e.commentAuthorId().equals(e.postAuthorId())) return;

            long newBalance = pointService.earnByRule(
                    e.commentAuthorId(),
                    PointRule.COMMENT_WRITE,   // 댓글 작성 적립 규칙
                    "POST",                    // ★ 이벤트에 commentId가 없으므로 POST 기준으로 남김
                    e.postId()
            );
            log.info("[POINT] COMMENT_WRITE granted. userId={}, postId={}, balance={}",
                    e.commentAuthorId(), e.postId(), newBalance);
        } catch (Exception ex) {
            log.error("[POINT][FAIL] COMMENT_WRITE userId={}, postId={}",
                    e.commentAuthorId(), e.postId(), ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPostCreated(PostCreatedEvent event) {
        pointService.earnByRule(
                event.getAuthorId(),
                PointRule.POST_WRITE,
                "POST",
                event.getPostId()
        );
    }
}