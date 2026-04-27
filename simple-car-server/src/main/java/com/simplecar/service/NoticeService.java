package com.simplecar.service;

import com.simplecar.model.entity.Notice;

import java.util.List;

public interface NoticeService {
    List<Notice> getNotices(Long userId);
}
