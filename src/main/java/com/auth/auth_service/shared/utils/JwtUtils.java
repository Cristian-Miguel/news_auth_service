package com.auth.auth_service.shared.utils;

import com.auth.auth_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtUtils {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.expired-date}")
    private long EXPIRATE_DATE;

    public String getToken(User user){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().getEnumName());
        return getToken(claims, user);
    }

    private String getToken(HashMap<String, Object> extraClaims, User user) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+EXPIRATE_DATE))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
//        return Jwts.SIG.HS256.key().build();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username =getUsernameFromToken(token);
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

    private Date getExpiration(String token){
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return getExpiration(token).before(new Date());
    }

}
