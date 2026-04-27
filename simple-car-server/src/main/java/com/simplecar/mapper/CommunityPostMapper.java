package com.simplecar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplecar.model.entity.CommunityPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CommunityPostMapper extends BaseMapper<CommunityPost> {
    @Select("SELECT p.*, u.nick_name as nickname, 'https://img01.yzcdn.cn/vant/cat.jpeg' as avatar, " +
            "(SELECT count(*) FROM community_post_like l WHERE l.post_id = p.id AND l.user_id = #{currentUserId}) as is_liked_count " +
            "FROM community_post p LEFT JOIN user u ON p.user_id = u.id ORDER BY p.create_time DESC")
    List<CommunityPost> selectPostListWithUserInfo(@Param("currentUserId") Long currentUserId);
}
