package com.simplecar.service;

import com.simplecar.model.entity.Notice;
import com.simplecar.result.PagedData;

public interface NoticeService {
    PagedData<Notice> getNotices(Long userId, Integer pageNum, Integer pageSize);
}
