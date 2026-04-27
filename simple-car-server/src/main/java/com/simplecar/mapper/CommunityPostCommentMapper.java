package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.CommunityPostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommunityPostCommentMapper extends BaseMapper<CommunityPostComment> {

    @Select("SELECT c.*, u.nick_name as nickname, 'https://img01.yzcdn.cn/vant/cat.jpeg' as avatar " +
            "FROM community_post_comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.create_time ASC")
    List<CommunityPostComment> selectCommentsWithUserInfo(@Param("postId") Long postId);
}
