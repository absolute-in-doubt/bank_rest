package com.example.bankcards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.List;

@Component
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authManager;
    private final DefaultBearerTokenResolver tokenResolver;
    private final AntPathMatcher pathMatcher;
    @Value("${spring.security.public_paths}")
    private List<String> SKIP_PATHS;

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
            if(SKIP_PATHS.stream().noneMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()))) {
                String jwt = tokenResolver.resolve(request);
                Authentication authToken = new BearerTokenAuthenticationToken(jwt);
                Authentication auth = authManager.authenticate(authToken); //expecting AuthManager to use JwtAuthenticationProvider
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);

        } catch (AuthenticationException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
