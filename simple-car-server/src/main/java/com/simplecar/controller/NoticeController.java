package com.simplecar.controller;

import com.simplecar.result.PagedData;
import com.simplecar.result.PageResponse;
import com.simplecar.util.SecurityUtils;
import com.simplecar.model.entity.Notice;
import com.simplecar.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通知管理")
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @Operation(summary = "获取当前用户通知列表")
    @GetMapping("/list")
    public PageResponse<Notice> getNotices(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        PagedData<Notice> page = noticeService.getNotices(userId, pageNum, pageSize);
        return PageResponse.success(page.rows(), page.total());
    }
}
