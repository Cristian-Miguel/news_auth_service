package com.auth.auth_service.shared.utils;

import com.auth.auth_service.dto.GenericErrorResponse;
import com.auth.auth_service.model.TokenBlackList;
import com.auth.auth_service.repository.TokenBlackListRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenBlackListRepository tokenBlackListRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String token = getTokenFromRequest(request);
        final String username;
        final String uri = request.getRequestURI();

        if(token==null &&
                (uri.equals("/api/auth/validate") || uri.equals("/api/auth/sign_out"))) {
            handleErrorResponse(response, "Token invalid.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        }

        if(token==null){
            filterChain.doFilter(request, response);
            return;
        }

        if(tokenBlackListRepository.existsById(token)){
            handleErrorResponse(response, "Token invalid.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        }

        try {
            username = jwtUtils.getUsernameFromToken(token);
        } catch (ExpiredJwtException ex){
            handleErrorResponse(response, "Token expired.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        } catch (MalformedJwtException ex){
            handleErrorResponse(response, "Token malformed.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        } catch (Exception ex){
            handleErrorResponse(response, "Token invalid.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtils.isTokenValid(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                handleErrorResponse(response, "Token invalid.",
                        HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private void handleErrorResponse(
            HttpServletResponse response, String message, int status, String error, String uri
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse =
        objectMapper.writeValueAsString(
                GenericErrorResponse.builder()
                        .timestamp(null)
                        .status(status)
                        .error(error)
                        .message(message)
                        .path(uri)
                        .build()
        );

        response.getWriter().write(jsonResponse);
    }
}
