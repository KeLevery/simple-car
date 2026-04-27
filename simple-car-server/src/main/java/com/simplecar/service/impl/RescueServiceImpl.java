package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.RescueRequest;
import com.simplecar.mapper.RescueRequestMapper;
import com.simplecar.service.RescueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RescueServiceImpl implements RescueService {
    private final RescueRequestMapper rescueRequestMapper;

    public RescueRequest create(RescueRequest request, Long userId) {
        request.setUserId(userId);
        request.setStatus(0);
        request.setCreateTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        rescueRequestMapper.insert(request);
        return request;
    }

    public List<RescueRequest> listByUser(Long userId) {
        return rescueRequestMapper.selectList(
                new LambdaQueryWrapper<RescueRequest>()
                        .eq(RescueRequest::getUserId, userId)
                        .orderByDesc(RescueRequest::getCreateTime)
        );
    }
}
