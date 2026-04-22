package com.example.usermanagementservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("=== GatewayAuthenticationFilter Debug ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Authorization Header: " + request.getHeader("Authorization"));
        System.out.println("Email Header: " + request.getHeader("Email"));
        System.out.println("UserEmail Header: " + request.getHeader("UserEmail"));

        String email = request.getHeader("UserEmail");
        String authorizationHeader = request.getHeader("Authorization");

        System.out.println("Email: " + email);
        System.out.println(SecurityContextHolder.getContext().getAuthentication());


        if (email == null && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            try {
                // Extract username from the JWT token provided by the Feign client
                email = jwtUtil.extractUsername(jwtToken);
                System.out.println("Email extracted from JWT token: " + email);
            } catch (Exception e) {
                System.out.println("Error extracting email from JWT: " + e.getMessage());
                // If token is invalid/expired, username remains null, and authentication is skipped
            }
        }

        System.out.println("Email to process: " + email);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("Successfully authenticated user: " + email);
                System.out.println("User authorities: " + userDetails.getAuthorities());
            } catch (Exception e) {
                System.out.println("Error loading user details for: " + email + " - " + e.getMessage());
            }
        } else {
            System.out.println("No UserEmail header found or user already authenticated");
        }

        filterChain.doFilter(request, response);
    }
}
