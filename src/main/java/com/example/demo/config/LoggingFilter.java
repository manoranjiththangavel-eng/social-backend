package com.example.demo.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingFilter implements Filter {

    // ✅ Proper logger instead of System.out.println
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String uri = req.getRequestURI();
        String method = req.getMethod();

        // ✅ Never log body for auth endpoints (passwords could be there)
        if (uri.contains("/auth/")) {
            logger.info("Incoming request: {} {} [body hidden]", method, uri);
        } else {
            logger.info("Incoming request: {} {}", method, uri);
        }

        chain.doFilter(request, response);

        logger.info("Response sent for: {} {}", method, uri);
    }
}