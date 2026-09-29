package com.simplecar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplecar.exception.BusinessException;
import com.simplecar.model.entity.CommunityPost;
import com.simplecar.model.entity.CommunityPostLike;
import com.simplecar.mapper.CommunityPostMapper;
import com.simplecar.mapper.CommunityPostLikeMapper;
import com.simplecar.result.PagedData;
import com.simplecar.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
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
    public PagedData<CommunityPost> listPosts(Long currentUserId, Integer pageNum, Integer pageSize) {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null ? 10 : Math.max(1, Math.min(pageSize, 100));
        IPage<CommunityPost> page = postMapper.selectPostListWithUserInfo(new Page<>(num, size), currentUserId);
        List<CommunityPost> posts = page.getRecords();
        posts.forEach(p -> {
            p.setIsLiked(p.getIsLikedCount() != null && p.getIsLikedCount() > 0);
        });
        return new PagedData<>(posts, page.getTotal());
    }

    @Override
    @Transactional
    public void toggleLike(Long postId, Long userId) {
        // 帖子不存在时直接拒绝，避免点赞脏数据触发外键裸 500 / 静默成功
        if (postMapper.selectById(postId) == null) {
            throw new BusinessException(404, "帖子不存在或已删除");
        }

        LambdaQueryWrapper<CommunityPostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityPostLike::getPostId, postId)
               .eq(CommunityPostLike::getUserId, userId);

        CommunityPostLike existing = likeMapper.selectOne(wrapper);

        if (existing == null) {
            CommunityPostLike like = new CommunityPostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            try {
                likeMapper.insert(like);
            } catch (DuplicateKeyException e) {
                // 并发下另一请求已点赞（uk_post_user 唯一索引兜底），无需重复计数
                return;
            }
            postMapper.incrementLikeCount(postId);
        } else {
            likeMapper.deleteById(existing.getId());
            postMapper.decrementLikeCount(postId);
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
