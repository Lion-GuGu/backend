package kr.ac.kumoh.likelion.gugu.community.web;

import kr.ac.kumoh.likelion.gugu.user.domain.User;
import kr.ac.kumoh.likelion.gugu.community.model.Comment;
import kr.ac.kumoh.likelion.gugu.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/community/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    public record CreateReq(String content, Long parentId) {}

    @PostMapping
    public Long create(@PathVariable Long postId,
                       @AuthenticationPrincipal Jwt jwt,
                       @RequestBody CreateReq req) {
        long userId = ((Number) jwt.getClaim("userId")).longValue();
        return commentService.create(postId, userId, req.content(), req.parentId());
    }

    @GetMapping
    public Page<Comment> list(@PathVariable Long postId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        return commentService.listByPost(postId, PageRequest.of(page, size));
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable Long postId,
                       @PathVariable Long commentId,
                       @AuthenticationPrincipal User me) {
        commentService.delete(commentId, me.getId());
    }
}