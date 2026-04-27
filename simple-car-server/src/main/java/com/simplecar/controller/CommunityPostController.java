package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.util.SecurityUtils;
import com.simplecar.model.entity.CommunityPost;
import com.simplecar.service.CommunityPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "社区管理")
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityPostController {
    private final CommunityPostService postService;

    @Operation(summary = "获取动态列表")
    @GetMapping("/post/list")
    public ApiResponse<List<CommunityPost>> list() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(postService.listPosts(userId));
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/post/like/{postId}")
    public ApiResponse<Void> toggleLike(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        postService.toggleLike(postId, userId);
        return ApiResponse.success();
    }

    @Operation(summary = "发布动态")
    @PostMapping("/post/create")
    public ApiResponse<CommunityPost> createPost(@RequestBody Map<String, String> params) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        String content = params.get("content");
        String images = params.get("images");
        if (content == null || content.trim().isEmpty()) {
            return ApiResponse.error("内容不能为空");
        }
        return ApiResponse.success(postService.createPost(userId, content.trim(), images));
    }
}
