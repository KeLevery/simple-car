package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplecar.model.entity.Notice;
import com.simplecar.mapper.NoticeMapper;
import com.simplecar.result.PagedData;
import com.simplecar.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper noticeMapper;

    public PagedData<Notice> getNotices(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.and(w -> w.isNull(Notice::getUserId).or().eq(Notice::getUserId, userId));
        } else {
            wrapper.isNull(Notice::getUserId);
        }
        wrapper.orderByDesc(Notice::getCreateTime);
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null ? 10 : Math.max(1, Math.min(pageSize, 100));
        Page<Notice> page = noticeMapper.selectPage(new Page<>(num, size), wrapper);
        return new PagedData<>(page.getRecords(), page.getTotal());
    }
}
