package com.example.infinnotest20tokens.Servlets;

import static jakarta.servlet.http.HttpServletResponse.*;

import com.example.infinnotest20tokens.Models.Token;
import com.example.infinnotest20tokens.Models.User;
import com.example.infinnotest20tokens.Services.LoginDAO;
import com.example.infinnotest20tokens.Services.TokensDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Random;

@MultipartConfig
@WebServlet(name = "loginServlet", value = "/login-servlet")
public class LoginServlet extends HttpServlet {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();

    LoginDAO loginDAO = new LoginDAO();
    TokensDAO tokensDAO = new TokensDAO();

    public LoginServlet() throws FileNotFoundException, URISyntaxException {}

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Part user = request.getPart("username");
        String userString = new BufferedReader(new InputStreamReader(user.getInputStream())).readLine();
        Part pass = request.getPart("password");
        String passString = new BufferedReader(new InputStreamReader(pass.getInputStream())).readLine();

        if (userString == null ||
                passString == null) {
            sendError(response, SC_UNAUTHORIZED, "Username or password is empty! USER NOT LOGGED IN!");
            return;
        }

        Integer salt = loginDAO.getUserSalt(userString);
        if (salt == null) {
            sendError(response, SC_UNAUTHORIZED, "This user does not exists! USER NOT LOGGED IN!");
            return;
        }
        String hashPass = DigestUtils.sha1Hex(passString.concat(salt.toString()));

        String path = request.getPathInfo();
        if (path != null && !path.equals("/"))
            sendError(response, SC_NOT_FOUND, "404 Not Found!");


        Integer userId = loginDAO.login(new User(userString, hashPass));
        if (userId == null) {
            String invalidCredentials = "Invalid credentials! USER NOT LOGGED IN!";
            sendError(response, SC_UNAUTHORIZED, invalidCredentials);
            return;
        }

        Token token = tokensDAO.getTokenByUserId(userId);

        if (token.token == null) {
            token = getToken(userId);
            tokensDAO.createToken(token);
        }

        response.setHeader("Authorization", "Bearer " + token.token);
    }

    void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        String errorMessage = gson.toJson(message);

        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(errorMessage.getBytes(StandardCharsets.UTF_8));
    }

    Token getToken(int userId) {
        int tokenNum = new Random().nextInt();
        String tokenHash = DigestUtils.sha1Hex(String.valueOf(tokenNum));
        String createdDate =  LocalDate.now().toString();
        String expirationDate = LocalDate.now().plus(Token.expirationPeriod, ChronoUnit.MILLIS).toString();

        return new Token(tokenHash, userId, createdDate, expirationDate);
    }
}
