package com.example.infinnotest20tokens.Filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;


public class LogFilter implements Filter {
    Logger logger = LogManager.getLogger(LogFilter.class);
//    FileHandler fh;
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.setPrettyPrinting().create();

    public LogFilter() {}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);

//        try {
//            URL url = this.getClass().getClassLoader().getResource("logConfig.properties");
//            InputStream inputStream = url.openStream();
//            String filePath = getLogOutputFile(inputStream);
//            fh = new FileHandler(filePath);
////            logger.addHandler(fh);
//            SimpleFormatter formatter = new SimpleFormatter();
//            fh.setFormatter(formatter);
//        } catch (SecurityException | IOException e) {
//            e.printStackTrace();
//        }
    }

    static String getLogOutputFile(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);

        return properties.getProperty("filePath");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long t1 = System.currentTimeMillis();
        filterChain.doFilter(servletRequest, servletResponse);
        long t2 = System.currentTimeMillis();

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpSession httpSession = httpServletRequest.getSession(false);

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        int status = httpServletResponse.getStatus();
        String statusString = status + ": ";

        String user = httpSession == null ? "" : (String) httpSession.getAttribute("user");
        String userString = user.equals("") ? "" : "user: " + user + " ";

        String executionTimeString = "time for execution: " + (t2 - t1) + "ms ";

        String methodString = httpServletRequest.getMethod() + " ";

        String pathInfo = httpServletRequest.getPathInfo() == null ? "" : httpServletRequest.getPathInfo();
        String pathString = httpServletRequest.getServletPath() + pathInfo;

        System.out.println(status);

        if (status >= 400) {
            logger.error("Error " + statusString +
                    userString +
                    executionTimeString +
                    methodString +
                    pathString);
            return;
        }

        logger.info(statusString +
                userString +
                executionTimeString +
                methodString +
                pathString);
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
