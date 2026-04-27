package com.simplecar.controller;

import com.simplecar.result.ApiResponse;
import com.simplecar.util.SecurityUtils;
import com.simplecar.model.entity.CommunityPostComment;
import com.simplecar.service.CommunityPostCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "社区评论")
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityPostCommentController {
    private final CommunityPostCommentService commentService;

    @Operation(summary = "获取动态评论列表")
    @GetMapping("/post/{postId}/comments")
    public ApiResponse<List<CommunityPostComment>> listComments(@PathVariable Long postId) {
        return ApiResponse.success(commentService.listComments(postId));
    }

    @Operation(summary = "发表评论")
    @PostMapping("/post/{postId}/comments")
    public ApiResponse<CommunityPostComment> createComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> params
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        String content = params.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ApiResponse.error("内容不能为空");
        }
        return ApiResponse.success(commentService.createComment(postId, userId, content.trim()));
    }
}
