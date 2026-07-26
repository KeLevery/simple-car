package com.simplecar.service.impl;

import com.simplecar.mapper.CommunityPostLikeMapper;
import com.simplecar.mapper.CommunityPostMapper;
import com.simplecar.model.entity.CommunityPostLike;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommunityPostServiceImplTest {

    @Test
    void likeInsertsRecordAndIncrementsAtomically() {
        CommunityPostMapper postMapper = mock(CommunityPostMapper.class);
        CommunityPostLikeMapper likeMapper = mock(CommunityPostLikeMapper.class);
        CommunityPostServiceImpl service = new CommunityPostServiceImpl(postMapper, likeMapper);

        when(likeMapper.selectOne(any())).thenReturn(null);

        service.toggleLike(5L, 7L);

        verify(likeMapper).insert(any(CommunityPostLike.class));
        verify(postMapper).incrementLikeCount(5L);
        verify(postMapper, never()).updateById(any());
        verify(postMapper, never()).selectById(any());
    }

    @Test
    void unlikeDeletesRecordAndDecrementsAtomically() {
        CommunityPostMapper postMapper = mock(CommunityPostMapper.class);
        CommunityPostLikeMapper likeMapper = mock(CommunityPostLikeMapper.class);
        CommunityPostServiceImpl service = new CommunityPostServiceImpl(postMapper, likeMapper);

        CommunityPostLike existing = new CommunityPostLike();
        existing.setId(11L);
        when(likeMapper.selectOne(any())).thenReturn(existing);

        service.toggleLike(5L, 7L);

        verify(likeMapper).deleteById(11L);
        verify(postMapper).decrementLikeCount(5L);
        verify(postMapper, never()).updateById(any());
    }
}
