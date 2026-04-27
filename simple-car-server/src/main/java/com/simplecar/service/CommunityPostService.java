package com.simplecar.service;

import com.simplecar.model.entity.CommunityPost;
import java.util.List;

public interface CommunityPostService {
    List<CommunityPost> listPosts(Long currentUserId);
    void toggleLike(Long postId, Long userId);
    CommunityPost createPost(Long userId, String content, String images);
}
