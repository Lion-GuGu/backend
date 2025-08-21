package kr.ac.kumoh.likelion.gugu.domain.community.web;

import org.springframework.security.oauth2.jwt.Jwt;
import kr.ac.kumoh.likelion.gugu.domain.user.User;
import kr.ac.kumoh.likelion.gugu.domain.community.model.Post;
import kr.ac.kumoh.likelion.gugu.domain.community.model.PostCategory;
import kr.ac.kumoh.likelion.gugu.domain.community.service.PostService;
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
    public Long create(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateReq req) {
        long userId = ((Number) jwt.getClaim("userId")).longValue(); // 안전 변환
        return postService.create(userId, req.category(), req.title(), req.content());
    }

    @GetMapping("/{postId}")
    public Post get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping
    public Page<Post> list(@RequestParam(required = false) PostCategory category,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        return postService.list(category, PageRequest.of(page, size));
    }

    @PatchMapping("/{postId}")
    public void update(@PathVariable Long postId,
                       @AuthenticationPrincipal User me,
                       @RequestBody UpdateReq req) {
        postService.update(postId, me.getId(), req.title(), req.content());
    }

    @DeleteMapping("/{postId}")
    public void delete(@PathVariable Long postId, @AuthenticationPrincipal User me) {
        postService.delete(postId, me.getId());
    }
}