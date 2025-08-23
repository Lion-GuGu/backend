package kr.ac.kumoh.likelion.gugu.community.event;

import kr.ac.kumoh.likelion.gugu.community.model.PostCategory;

/** 댓글(답변) 생성 시 발행되는 도메인 이벤트 */
public record CommentCreatedEvent(
        Long commentAuthorId,
        Long postId,
        PostCategory postCategory,
        Long postAuthorId       // 자답(자기 글에 자기 댓글) 가드에 사용 가능
) {}