package com.simplecar.service;

import com.simplecar.model.entity.CommunityPostComment;

import java.util.List;

public interface CommunityPostCommentService {
    List<CommunityPostComment> listComments(Long postId);

    CommunityPostComment createComment(Long postId, Long userId, String content);
}
