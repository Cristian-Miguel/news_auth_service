package com.auth.auth_service.shared.infrastructure.utils;

import com.auth.auth_service.shared.infrastructure.adapter.input.rest.data.response.GenericErrorResponse;
import com.auth.auth_service.role.infrastructure.constant.RoleEnum;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String token = getTokenFromRequest(request);
        final String username;
        final String uri = request.getRequestURI();
        RoleEnum role;

        //uri.equals("/api/auth/sign_out"))
        if(token==null &&
                uri.equals("/api/auth/validate")) {
            handleErrorResponse(response, "Token invalid.",
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), uri);
            return;
        }

        if(token==null){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            username = jwtUtils.getUsernameFromToken(token);

            Map<String, Object> claims = jwtUtils.getAllClaims(token);

            role = RoleEnum.valueOf( (String) claims.get("role") );

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
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.getCode()));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities//userDetails.getAuthorities(),
                    );

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
