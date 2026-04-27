package com.simplecar.service.impl;

import com.simplecar.mapper.CommunityPostCommentMapper;
import com.simplecar.mapper.CommunityPostMapper;
import com.simplecar.model.entity.CommunityPost;
import com.simplecar.model.entity.CommunityPostComment;
import com.simplecar.service.CommunityPostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostCommentServiceImpl implements CommunityPostCommentService {
    private final CommunityPostCommentMapper commentMapper;
    private final CommunityPostMapper postMapper;

    @Override
    public List<CommunityPostComment> listComments(Long postId) {
        return commentMapper.selectCommentsWithUserInfo(postId);
    }

    @Override
    @Transactional
    public CommunityPostComment createComment(Long postId, Long userId, String content) {
        CommunityPostComment comment = new CommunityPostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);

        CommunityPost post = postMapper.selectById(postId);
        if (post != null) {
            Integer current = post.getCommentCount() == null ? 0 : post.getCommentCount();
            post.setCommentCount(current + 1);
            postMapper.updateById(post);
        }

        return comment;
    }
}
