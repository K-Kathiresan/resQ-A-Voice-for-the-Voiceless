package com.resq.resq.security;

import com.resq.resq.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {

        String path = request.getServletPath();

        System.out.println("Request Path: " + path);

        // SKIP JWT CHECK FOR AUTH APIs AND UPLOADED IMAGES
        if (
                path.startsWith("/api/auth/") ||
                path.startsWith("/uploads/")
        ) {

                filterChain.doFilter(request, response);
                return;
        }

        String authHeader = request.getHeader("Authorization");

        System.out.println("JWT Filter Executed");
        System.out.println("Authorization Header: " + authHeader);

        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

                token = authHeader.substring(7);

                email = jwtUtil.extractEmail(token);

                System.out.println("Extracted Email: " + email);
        }

        if (
                email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null
        ) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(token, userDetails.getUsername())) {

                System.out.println("JWT Token Valid");

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authenticationToken);
                }
        }

        filterChain.doFilter(request, response);
        }
}