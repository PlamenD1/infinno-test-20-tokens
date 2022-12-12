package com.example.infinnotest20tokens.Services;

import com.example.infinnotest20tokens.Models.Token;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TokenClearer {

    TokensDAO dao = new TokensDAO();
    public TokenClearer() throws FileNotFoundException {}

    void startClearing() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Token> tokens = dao.getAllToken();
                for (Token token : tokens) {
                    LocalDate tokenExpDate = LocalDate.parse(token.expiration_date);
                    if (!tokenExpDate.isBefore(LocalDate.now()))
                        continue;

                    dao.deleteToken(token.token);
                }

            }
        },86400000L, 86400000L);
    }
}
