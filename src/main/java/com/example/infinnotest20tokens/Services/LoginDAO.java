package com.example.infinnotest20tokens.Services;

import com.example.infinnotest20tokens.Interfaces.LoginMapper;
import com.example.infinnotest20tokens.Models.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.Objects;

public class LoginDAO {
    SqlSessionFactory sessionFactory;

    public LoginDAO() throws FileNotFoundException, URISyntaxException {
        sessionFactory = new SqlSessionFactoryBuilder().build(new FileReader(new File(Objects.requireNonNull(this.getClass().getResource("/config.xml")).getFile())));
    }

    public Integer login(User user) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(LoginMapper.class);
            return mapper.login(user);
        }
    }

    public Integer getUserSalt(String username) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(LoginMapper.class);
            return mapper.getUserSalt(username);
        }
    }

}
