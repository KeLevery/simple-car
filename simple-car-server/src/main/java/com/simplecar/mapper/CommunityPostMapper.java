package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.CommunityPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface CommunityPostMapper extends BaseMapper<CommunityPost> {
    @Select("SELECT p.*, u.nick_name as nickname, 'https://img01.yzcdn.cn/vant/cat.jpeg' as avatar, " +
            "(SELECT count(*) FROM community_post_like l WHERE l.post_id = p.id AND l.user_id = #{currentUserId}) as is_liked_count " +
            "FROM community_post p LEFT JOIN user u ON p.user_id = u.id ORDER BY p.create_time DESC")
    List<CommunityPost> selectPostListWithUserInfo(@Param("currentUserId") Long currentUserId);

    @Update("UPDATE community_post SET like_count = like_count + 1 WHERE id = #{postId}")
    int incrementLikeCount(@Param("postId") Long postId);

    @Update("UPDATE community_post SET like_count = like_count - 1 WHERE id = #{postId} AND like_count > 0")
    int decrementLikeCount(@Param("postId") Long postId);

    @Update("UPDATE community_post SET comment_count = COALESCE(comment_count, 0) + 1 WHERE id = #{postId}")
    int incrementCommentCount(@Param("postId") Long postId);
}
