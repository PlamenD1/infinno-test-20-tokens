package com.example.infinnotest20tokens.Filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

public class AuthFilter implements Filter {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();

    public AuthFilter() {}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!isAuthorized((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse)) {
            sendError((HttpServletResponse) servletResponse, SC_FORBIDDEN, "USER IS NOT LOGGED IN!");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    boolean isAuthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            sendError(response, SC_FORBIDDEN, "Unauthorized user!");
            return false;
        }

        return true;
    }
    void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        String errorMessage = gson.toJson(message);

        response.addHeader("Content-Type", "application/json");
        response.getOutputStream().write(errorMessage.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
