package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplecar.exception.BusinessException;
import com.simplecar.mapper.CommunityPostCommentMapper;
import com.simplecar.mapper.CommunityPostMapper;
import com.simplecar.model.entity.CommunityPostComment;
import com.simplecar.result.PagedData;
import com.simplecar.service.CommunityPostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommunityPostCommentServiceImpl implements CommunityPostCommentService {
    private final CommunityPostCommentMapper commentMapper;
    private final CommunityPostMapper postMapper;

    @Override
    public PagedData<CommunityPostComment> listComments(Long postId, Integer pageNum, Integer pageSize) {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null ? 10 : Math.max(1, Math.min(pageSize, 100));
        IPage<CommunityPostComment> page = commentMapper.selectCommentsWithUserInfo(new Page<>(num, size), postId);
        return new PagedData<>(page.getRecords(), page.getTotal());
    }

    @Override
    @Transactional
    public CommunityPostComment createComment(Long postId, Long userId, String content) {
        // 帖子不存在时直接拒绝，避免评论落到无效 postId
        if (postMapper.selectById(postId) == null) {
            throw new BusinessException(404, "帖子不存在或已删除");
        }

        CommunityPostComment comment = new CommunityPostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);

        postMapper.incrementCommentCount(postId);

        return comment;
    }
}
