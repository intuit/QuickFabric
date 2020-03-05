package com.intuit.quickfabric.commons.security;

import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.exceptions.QuickFabricUnauthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    private JWTTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        try {
            if (!request.getServletPath().toLowerCase().endsWith(ApiUrls.LOGIN_SERVICE_PATH)
                    && token != null
                    && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (QuickFabricUnauthenticatedException ex) {
            SecurityContextHolder.clearContext();
            response.resetBuffer();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().print("{\"message\":\"" + ex.getMessage() + "\"}");
            response.flushBuffer();
            return;
        }

        filterChain.doFilter(request, response);
    }
}
