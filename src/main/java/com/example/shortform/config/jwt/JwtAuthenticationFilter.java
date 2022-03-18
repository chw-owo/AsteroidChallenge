package com.example.shortform.config.jwt;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {



    private final JwtAuthenticationProvider jwtAuthenticationProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 토큰의 인증 정보를 SecurityContext에 저장하는 역할 수행
        String jwt = jwtAuthenticationProvider.resolveToken(request);
        String requestURI = request.getRequestURI();

        if (StringUtils.hasText(jwt) && jwtAuthenticationProvider.validateToken(jwt)) {
            Authentication authentication = jwtAuthenticationProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //log.info("saved login info {}", authentication.getName());
        }


        filterChain.doFilter(request, response);
    }
}