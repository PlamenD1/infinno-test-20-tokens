package com.example.infinnotest20tokens.Interfaces;

import com.example.infinnotest20tokens.Models.Comment;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommentsMapper {
    @Select("SELECT * FROM Comments WHERE post_id = #{id}")
    List<Comment> getCommentsByPost(int id);
}
