package com.haowujiang.sanguosha.infrastructure.security.filter;

import com.haowujiang.sanguosha.infrastructure.security.constants.UserContextHeaders;
import com.haowujiang.sanguosha.infrastructure.security.context.HeaderAuthenticatedUser;
import com.haowujiang.sanguosha.infrastructure.security.context.SecurityRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class HeaderAuthenticationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticate(request);
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request) {
        String userIdHeader = request.getHeader(UserContextHeaders.USER_ID);
        String roleHeader = request.getHeader(UserContextHeaders.USER_ROLE);
        if (!StringUtils.hasText(userIdHeader) || !StringUtils.hasText(roleHeader)) {
            return;
        }

        try {
            Long userId = Long.valueOf(userIdHeader);
            Integer role = Integer.valueOf(roleHeader);
            SecurityRole securityRole = SecurityRole.of(role);
            if (securityRole == null) {
                return;
            }
            HeaderAuthenticatedUser principal = new HeaderAuthenticatedUser(userId, role);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority(securityRole.getAuthority()))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (NumberFormatException ignored) {
            SecurityContextHolder.clearContext();
        }
    }
}

