package com.example.infinnotest20tokens.Servlets;

import com.example.infinnotest20tokens.Models.Token;
import com.example.infinnotest20tokens.Models.User;
import com.example.infinnotest20tokens.Services.RegisterDAO;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static jakarta.servlet.http.HttpServletResponse.*;

@MultipartConfig
@WebServlet(name = "registerServlet", value = "/register-servlet")

public class RegisterServlet extends HttpServlet {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();
    RegisterDAO registerDAO = new RegisterDAO();
    TokensDAO tokensDAO = new TokensDAO();

    public RegisterServlet() throws FileNotFoundException {}
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

        Random r = new Random();
        int salt = r.nextInt();
        String hashPass = DigestUtils.sha1Hex(passString.concat(String.valueOf(salt)));

        String path = request.getPathInfo();
        if (path != null && !path.equals("/"))
            sendError(response, SC_NOT_FOUND, "404 Not Found!");

        User userToRegister = new User(userString, hashPass, salt);
        int rowsAffected = registerDAO.register(userToRegister);

        if (rowsAffected != 1) {
            String existingAccount = "Account with these credentials already exists!";
            sendError(response, SC_UNAUTHORIZED, existingAccount);
            return;
        }

        Token token = getToken(userToRegister.id);
        tokensDAO.createToken(token);
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
        String expirationDate = LocalDate.now().plusDays(Token.expirationPeriodInDays).toString();

        return new Token(tokenHash, userId, createdDate, expirationDate);
    }
}
