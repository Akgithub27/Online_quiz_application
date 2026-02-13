package com.online_quiz.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractToken(request);

            if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
                String email = jwtProvider.getEmailFromToken(jwt);
                String role = jwtProvider.getRoleFromToken(jwt);

                if (email != null && role != null) {
                    String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority(authority))
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Set Spring Security authentication for user: {} with authority: {}", email, authority);
                } else {
                    log.warn("JWT token missing email or role claim");
                }
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
