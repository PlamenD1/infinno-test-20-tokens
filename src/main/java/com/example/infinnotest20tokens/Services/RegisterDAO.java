package com.example.infinnotest20tokens.Services;

import com.example.infinnotest20tokens.Interfaces.RegisterMapper;
import com.example.infinnotest20tokens.Models.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;

public class RegisterDAO {

    SqlSessionFactory sessionFactory;
    public RegisterDAO() throws FileNotFoundException {
        FileReader fr = new FileReader(new File(Objects.requireNonNull(this.getClass().getResource("/config.xml")).getFile()));
        sessionFactory = new SqlSessionFactoryBuilder().build(fr);
    }
    public int register(User user) {
        try (SqlSession conn = sessionFactory.openSession()) {
            var mapper = conn.getMapper(RegisterMapper.class);
            int result = mapper.register(user);
            conn.commit();

            return result;
        }
    }
}


