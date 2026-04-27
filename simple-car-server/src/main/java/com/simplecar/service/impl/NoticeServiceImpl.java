package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.Notice;
import com.simplecar.mapper.NoticeMapper;
import com.simplecar.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper noticeMapper;

    public List<Notice> getNotices(Long userId) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.and(w -> w.isNull(Notice::getUserId).or().eq(Notice::getUserId, userId));
        } else {
            wrapper.isNull(Notice::getUserId);
        }
        wrapper.orderByDesc(Notice::getCreateTime);
        return noticeMapper.selectList(wrapper);
    }
}
