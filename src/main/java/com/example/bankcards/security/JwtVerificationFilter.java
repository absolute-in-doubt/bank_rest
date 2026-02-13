package com.example.bankcards.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthenticationManager authManager;
    private final DefaultBearerTokenResolver tokenResolver;
    private final AntPathMatcher pathMatcher;
    @Value("${spring.security.paths.public}")
    private String[] SKIP_PATHS;

    @Autowired
    public JwtVerificationFilter(AuthenticationManager authManager,
                                 DefaultBearerTokenResolver tokenResolver) {
        this.authManager = authManager;
        this.tokenResolver = tokenResolver;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            logger.trace("Received request to {}", request.getRequestURI());

            if(Arrays.stream(SKIP_PATHS).noneMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()))) {
                logger.trace("Verifying JWT for request to {}", request.getRequestURI());

                String jwt = tokenResolver.resolve(request);
                Authentication authToken = new BearerTokenAuthenticationToken(jwt);
                Authentication auth = authManager.authenticate(authToken); //expecting AuthManager to use JwtAuthenticationProvider
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);

        } catch (AuthenticationException | IllegalArgumentException e){
            logger.trace("Request to {} wasn't authorized: {}", request.getRequestURI(), e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
