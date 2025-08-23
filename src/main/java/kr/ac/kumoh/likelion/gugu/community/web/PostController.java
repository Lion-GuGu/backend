package kr.ac.kumoh.likelion.gugu.community.web;

import org.springframework.security.oauth2.jwt.Jwt;
import kr.ac.kumoh.likelion.gugu.user.domain.User;
import kr.ac.kumoh.likelion.gugu.community.model.Post;
import kr.ac.kumoh.likelion.gugu.community.model.PostCategory;
import kr.ac.kumoh.likelion.gugu.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    public record CreateReq(String title, String content, PostCategory category) {}
    public record UpdateReq(String title, String content) {}

    @PostMapping
    public Long create(
            @AuthenticationPrincipal(expression = "claims['userId']") Object uid,  // 🔁 변경
            @RequestBody CreateReq req
    ) {
        long userId = toUserId(uid);
        return postService.create(userId, req.category(), req.title(), req.content());
    }

    @GetMapping("/{postId}")
    public Post get(@PathVariable Long postId) {
        return postService.view(postId);
    }

    @GetMapping
    public Page<Post> list(@RequestParam(required = false) PostCategory category,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        return postService.list(category, PageRequest.of(page, size));
    }

    @PatchMapping("/{postId}")
    public void update(@PathVariable Long postId,
                       @AuthenticationPrincipal(expression = "claims['userId']") Object uid,  // 🔁 변경
                       @RequestBody UpdateReq req) {
        postService.update(postId, toUserId(uid), req.title(), req.content());
    }

    @DeleteMapping("/{postId}")
    public void delete(@PathVariable Long postId,
                       @AuthenticationPrincipal(expression = "claims['userId']") Object uid) { // 🔁 변경
        postService.delete(postId, toUserId(uid));
    }

    // ---- helpers ----
    private long toUserId(Object uid) {
        if (uid == null) throw new IllegalStateException("토큰에 userId 클레임이 없습니다.");
        if (uid instanceof Number n) return n.longValue();   // Integer/Long 모두 안전 변환
        return Long.parseLong(uid.toString());
    }
}