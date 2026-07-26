package com.simplecar.service;

import com.simplecar.model.entity.CommunityPostComment;
import com.simplecar.result.PagedData;

public interface CommunityPostCommentService {
    PagedData<CommunityPostComment> listComments(Long postId, Integer pageNum, Integer pageSize);

    CommunityPostComment createComment(Long postId, Long userId, String content);
}
