package com.haowujiang.sanguosha.infrastructure.security.filter;

import com.haowujiang.sanguosha.infrastructure.security.context.HeaderAuthenticatedUser;
import com.haowujiang.sanguosha.infrastructure.enums.SecurityRole;
import com.haowujiang.sanguosha.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticate(request);
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            return;
        }
        HeaderAuthenticatedUser user = jwtTokenProvider.parseToken(authorization.substring(BEARER_PREFIX.length()));
        if (user == null) {
            return;
        }
        SecurityRole securityRole = SecurityRole.of(user.getRole());
        if (securityRole == null) {
            return;
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(securityRole.getAuthority()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
