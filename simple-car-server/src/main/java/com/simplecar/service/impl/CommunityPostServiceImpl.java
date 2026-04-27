package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simplecar.model.entity.CommunityPost;
import com.simplecar.model.entity.CommunityPostLike;
import com.simplecar.mapper.CommunityPostMapper;
import com.simplecar.mapper.CommunityPostLikeMapper;
import com.simplecar.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {
    private final CommunityPostMapper postMapper;
    private final CommunityPostLikeMapper likeMapper;

    @Override
    public List<CommunityPost> listPosts(Long currentUserId) {
        List<CommunityPost> posts = postMapper.selectPostListWithUserInfo(currentUserId);
        posts.forEach(p -> {
            p.setIsLiked(p.getIsLikedCount() != null && p.getIsLikedCount() > 0);
        });
        return posts;
    }

    @Override
    @Transactional
    public void toggleLike(Long postId, Long userId) {
        LambdaQueryWrapper<CommunityPostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityPostLike::getPostId, postId)
               .eq(CommunityPostLike::getUserId, userId);

        CommunityPostLike existing = likeMapper.selectOne(wrapper);
        CommunityPost post = postMapper.selectById(postId);

        if (existing == null) {
            CommunityPostLike like = new CommunityPostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            likeMapper.insert(like);

            if (post != null) {
                post.setLikeCount(post.getLikeCount() + 1);
                postMapper.updateById(post);
            }
        } else {
            likeMapper.deleteById(existing.getId());
            if (post != null && post.getLikeCount() > 0) {
                post.setLikeCount(post.getLikeCount() - 1);
                postMapper.updateById(post);
            }
        }
    }

    @Override
    @Transactional
    public CommunityPost createPost(Long userId, String content, String images) {
        CommunityPost post = new CommunityPost();
        post.setUserId(userId);
        post.setContent(content);
        post.setImages(images);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setShareCount(0);
        post.setIsHot(0);
        post.setCreateTime(LocalDateTime.now());
        postMapper.insert(post);
        return post;
    }
}
