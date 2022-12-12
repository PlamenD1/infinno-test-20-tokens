package com.example.infinnotest20tokens.Services;

import com.example.infinnotest20tokens.Models.Comment;
import com.example.infinnotest20tokens.Interfaces.CommentsMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

public class CommentsDAO {
    SqlSessionFactory sessionFactory;
    public CommentsDAO() throws FileNotFoundException {
        sessionFactory = new SqlSessionFactoryBuilder().build(new FileReader(new File(Objects.requireNonNull(this.getClass().getResource("/config.xml")).getFile())));
    }

    public List<Comment> getCommentsByPost(int id) {
         try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(CommentsMapper.class);
            return mapper.getCommentsByPost(id);
        }
    }
}
