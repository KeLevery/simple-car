package com.simplecar.service;

import com.simplecar.model.entity.CommunityPost;
import com.simplecar.result.PagedData;

public interface CommunityPostService {
    PagedData<CommunityPost> listPosts(Long currentUserId, Integer pageNum, Integer pageSize);
    void toggleLike(Long postId, Long userId);
    CommunityPost createPost(Long userId, String content, String images);
}
