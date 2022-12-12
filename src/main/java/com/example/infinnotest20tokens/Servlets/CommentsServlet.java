package com.example.infinnotest20tokens.Servlets;

import static jakarta.servlet.http.HttpServletResponse.*;

import com.example.infinnotest20tokens.Models.Comment;
import com.example.infinnotest20tokens.Utils.PathInfo;
import com.example.infinnotest20tokens.Services.CommentsDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "commentsServlet", value = "/comments-servlet")
public class CommentsServlet extends HttpServlet {
    final Map<String, Pattern> patterns = new HashMap<>();
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();
    CommentsDAO dao = new CommentsDAO();

    public CommentsServlet() throws FileNotFoundException {}

    public void init() {
        patterns.put("emptyPath", Pattern.compile("\\/")); //get all comments for post
    }

    PathInfo getPath(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null || path.equals("/"))
            return new PathInfo("emptyPath", null);

        for (var entry : patterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(path);

            if (matcher.matches()) {
                return new PathInfo(entry.getKey(), matcher);
            }
        }

        return new PathInfo("404", null);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthorized(request, response))
            return;

        PathInfo pathInfo = getPath(request);
        String pathName = pathInfo.pathName;

        switch (pathName) {
            case "emptyPath": {
                int id = Integer.parseInt(request.getParameter("postId"));

                List<Comment> result = dao.getCommentsByPost(id);

                sendResponse(response, result);
            }
            default: sendError(response, SC_NOT_FOUND, "404 Not Found!");
        }
    }

    void sendResponse(HttpServletResponse response, Object o) throws IOException {
        String json = gson.toJson(o);

        response.setStatus(200);
        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
    }

    void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        String errorMessage = gson.toJson(message);

        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(errorMessage.getBytes(StandardCharsets.UTF_8));
    }

    boolean isAuthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            sendError(response, 403, "Unauthorized user!");
            return false;
        }

        return true;
    }
}
