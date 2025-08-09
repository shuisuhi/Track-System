package org.example.tlias.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.tlias.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    private final String secretKey = "dongchuan";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter被调用，请求URL: " + request.getRequestURL());
        String authHeader = request.getHeader("Authorization");
        String token = null;
        
        // 检查Authorization头是否存在且以Bearer开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 提取Bearer后的token
        }
        
        // 如果Authorization头中没有token，则从查询参数中获取
        if ((token == null || token.isEmpty()) && request.getParameter("token") != null) {
            token = request.getParameter("token");
        }
        
        if (token != null && !token.isEmpty()) {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.get("username", String.class);
                System.out.println("JWT解析出的用户名: " + username);
                if (username != null) {
                    System.out.println("UserDetailsService是否为空: " + (userDetailsService == null));
                    if (userDetailsService != null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        System.out.println("加载的用户详情: " + userDetails);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                System.out.println("Invalid token: " + e.getMessage());  // Debugging log
            }
        }
        System.out.println("JwtAuthenticationFilter执行完成");
        filterChain.doFilter(request, response);
    }
}
