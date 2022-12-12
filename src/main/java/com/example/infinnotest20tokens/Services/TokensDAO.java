package com.example.infinnotest20tokens.Services;

import com.example.infinnotest20tokens.Interfaces.TokensMapper;
import com.example.infinnotest20tokens.Models.Token;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

public class TokensDAO {

    SqlSessionFactory sessionFactory;
    public TokensDAO() throws FileNotFoundException {
        FileReader fr = new FileReader(new File(Objects.requireNonNull(this.getClass().getResource("/config.xml")).getFile()));
        sessionFactory = new SqlSessionFactoryBuilder().build(fr);
    }

    public List<Token> getAllToken() {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(TokensMapper.class);
            return mapper.getAllTokens();
        }
    }

    public Token getToken(String token) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(TokensMapper.class);
            return mapper.getToken(token);
        }
    }

    public Token getTokenByUserId(Integer userId) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(TokensMapper.class);
            return mapper.getTokenByUserId(userId);
        }
    }

    public int createToken(Token token) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(TokensMapper.class);
            int result = mapper.createToken(token);
            conn.commit();

            return result;
        }
    }

    public int deleteToken(String token) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(TokensMapper.class);
            int result = mapper.deleteToken(token);
            conn.commit();

            return result;
        }
    }
}


