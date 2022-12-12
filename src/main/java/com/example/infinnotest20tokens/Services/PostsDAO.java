package com.example.infinnotest20tokens.Services;

import com.example.infinnotest20tokens.Models.Comment;
import com.example.infinnotest20tokens.Interfaces.PostsMapper;
import com.example.infinnotest20tokens.Models.Post;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

public class PostsDAO {

       SqlSessionFactory sessionFactory;
       public PostsDAO() throws FileNotFoundException {
              FileReader fr = new FileReader(new File(Objects.requireNonNull(this.getClass().getResource("/config.xml")).getFile()));
              sessionFactory = new SqlSessionFactoryBuilder().build(fr);
       }

       public List<Post> getAllPosts() {
              try (SqlSession conn = sessionFactory.openSession()) {
                     var mapper = conn.getMapper(PostsMapper.class);
                     return mapper.getAllPosts();
              }
       }

       public Post getPostById(int id) {
              try (SqlSession conn = sessionFactory.openSession()) {
                     var mapper = conn.getMapper(PostsMapper.class);
                     return mapper.getPostById(id);
              }
       }

       public List<Comment> getCommentsForPost(int id) {
              try (SqlSession conn = sessionFactory.openSession()) {
                     var mapper = conn.getMapper(PostsMapper.class);
                     return mapper.getCommentsForPost(id);
              }
       }

       public int addPost(Post post) {
              try (SqlSession conn = sessionFactory.openSession()) {
                     var mapper = conn.getMapper(PostsMapper.class);
                     int result = mapper.addPost(post);
                     conn.commit();

                     return result;
              }
       }

       public int updatePost(Post post) {
              try (SqlSession conn = sessionFactory.openSession()) {
                     var mapper = conn.getMapper(PostsMapper.class);
                     int result = mapper.updatePost(post);
                     conn.commit();

                     return result;
              }
       }

       public int deletePost(int id) {
              try (SqlSession conn = sessionFactory.openSession()) {
                     var mapper = conn.getMapper(PostsMapper.class);
                     int result = mapper.deletePost(id);
                     conn.commit();

                     return result;
              }
       }
}