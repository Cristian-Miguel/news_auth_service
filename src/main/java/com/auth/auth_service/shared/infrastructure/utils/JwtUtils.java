package com.auth.auth_service.shared.infrastructure.utils;

import com.auth.auth_service.user.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.expired-date}")
    private long EXPIRED_DATE;

    @Value("${jwt.expired-date-refresh}")
    private long EXPIRED_DATE_REFRESH;

    @Value("${jwt.expired-date-email-validate}")
    private long EXPIRED_DATE_EMAIL_VALIDATE;

    private static final SecureDigestAlgorithm<SecretKey, ?> algorithm = Jwts.SIG.HS256;

    public String getToken(User user){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().getEnumName().getCode());
        return getToken(claims, user, new Date(System.currentTimeMillis()+EXPIRED_DATE));
    }

    public Map<String, Object> getRefreshToken(String uuid, User user){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("uuid", uuid);
        claims.put("role", user.getRole().getEnumName().getCode());
        return getRefreshToken(claims, user);
    }

    public String getTokenToValidateEmail(User user){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        return getToken(claims, user, new Date(System.currentTimeMillis()+EXPIRED_DATE_EMAIL_VALIDATE));
    }

    private String getToken(HashMap<String, Object> extraClaims, User user, Date expired) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expired)
                .signWith(getKey(), algorithm)
                .compact();
    }

    private Map<String, Object> getRefreshToken(HashMap<String, Object> extraClaims, User user){
        Map<String, Object> refreshToken = new HashMap<>();

        Date expiredDate = new Date(System.currentTimeMillis()+EXPIRED_DATE_REFRESH);

        LocalDateTime expired = expiredDate
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        String token = getToken(extraClaims, user, expiredDate);

        refreshToken.put("expired", expired);
        refreshToken.put("refresh", token);

        return refreshToken;
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    //   return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    //    return Jwts.SIG.HS256.key().build();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Claims getAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = getAllClaims(token);

        return claimsResolver.apply(claims);
    }

    public Date getExpiration(String token){
        return getClaim(token, Claims::getExpiration);
    }

    public String getUuidFromToken(String token) {
        Claims claims = getAllClaims(token);
        String uuid = claims.get("uuid", String.class);
        return uuid;
    }

    private boolean isTokenExpired(String token){
        return getExpiration(token).before(new Date());
    }

}
