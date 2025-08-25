package kr.ac.kumoh.likelion.gugu.community.web;

import kr.ac.kumoh.likelion.gugu.user.domain.User;
import kr.ac.kumoh.likelion.gugu.community.model.Comment;
import kr.ac.kumoh.likelion.gugu.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    public record CreateReq(String content, Long parentId) {}

    @PostMapping("/posts/{postId}/comments")
    public Long create(@PathVariable Long postId,
                       @AuthenticationPrincipal Jwt jwt,
                       @RequestBody CreateReq req) {
        long userId = ((Number) jwt.getClaim("userId")).longValue();
        return commentService.create(postId, userId, req.content(), req.parentId());
    }

    @GetMapping("/posts/{postId}/comments")
    public Page<Comment> list(@PathVariable Long postId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        return commentService.listByPost(postId, PageRequest.of(page, size));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal(expression = "claims['userId']") Object uid // Object로 받기
    ) {
        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."); // 401
        }
        long actorId;
        if (uid instanceof Number n) actorId = n.longValue();
        else if (uid instanceof String s) actorId = Long.parseLong(s);
        else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자 정보");

        commentService.delete(commentId, actorId);
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/accept")
    public ResponseEntity<Void> acceptAnswer(@AuthenticationPrincipal(expression = "claims['userId']") Number uid,
                                             @PathVariable Long postId,
                                             @PathVariable Long commentId) {
        commentService.acceptAnswer(postId, commentId, uid.longValue());
        return ResponseEntity.noContent().build();
    }
}