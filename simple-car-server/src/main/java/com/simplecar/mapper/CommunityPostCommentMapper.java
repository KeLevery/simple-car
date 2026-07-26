package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplecar.model.entity.CommunityPostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommunityPostCommentMapper extends BaseMapper<CommunityPostComment> {

    @Select("SELECT c.*, u.nick_name as nickname, 'https://img01.yzcdn.cn/vant/cat.jpeg' as avatar " +
            "FROM community_post_comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.create_time ASC")
    IPage<CommunityPostComment> selectCommentsWithUserInfo(Page<CommunityPostComment> page, @Param("postId") Long postId);
}
