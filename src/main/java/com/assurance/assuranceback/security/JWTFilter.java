package com.assurance.assuranceback.security;


import com.assurance.assuranceback.Services.UserServices.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JWTFilter  {

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;



}
