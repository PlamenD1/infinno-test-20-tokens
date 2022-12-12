package com.example.infinnotest20tokens.Interfaces;

import com.example.infinnotest20tokens.Models.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface LoginMapper {
    @Select("SELECT id FROM users WHERE username = #{user.username} AND password = #{user.password}")
    Integer login(@Param("user") User user);

    @Select("SELECT salt FROM users WHERE username = #{username}")
    Integer getUserSalt(@Param("username") String username);
}
