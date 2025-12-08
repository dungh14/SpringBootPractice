package vn.dungjava.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import vn.dungjava.common.TokenType;
import vn.dungjava.exception.InvalidDataException;
import vn.dungjava.service.JwtService;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import static vn.dungjava.common.TokenType.ACCESS_TOKEN;
import static vn.dungjava.common.TokenType.REFRESH_TOKEN;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryMinutes}")
    private Long expiryMinutes;

    @Value("${jwt.expiryDay}")
    private Long expiryDay;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Override
    public String generateAccessToken(String username, List<String> authorities) {
        log.info("Generate Access Token for username: {} with authorities: {}", username, authorities);

        Map<String,Object> claims = new HashMap<>();
        claims.put("role", authorities);

        return generateToken(claims, username);
    }

    @Override
    public String generateRefreshToken(String username, List<String> authorities) {
        log.info("Generate Refresh Token for username: {} with authorities: {}", username, authorities);

        Map<String,Object> claims = new HashMap<>();
        claims.put("role", authorities);

        return generateRefreshToken(claims, username);
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        log.info("Extract Username for token: {} with type: {}", token, type);

        return extractClaims(type, token, Claims::getSubject);
    }

    private <T> T extractClaims(TokenType type, String token, Function<Claims, T> claimsResolver)  {
        final Claims claims = extraAllClaim(token, type);
        return claimsResolver.apply(claims);
    }

    private Claims extraAllClaim(String token, TokenType type) {
        try {
            Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access denied! error: " + e.getMessage());
        }
    }


    private String generateToken(Map<String, Object> claims, String username) {
        log.info("Generate Token for user: {} with claims: {}", username, claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiryMinutes))
                .signWith(getKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String, Object> claims, String username) {
        log.info("Generate Token for user: {} with claims: {}", username, claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .signWith(getKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type) {
        switch (type) {
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default -> {throw new InvalidDataException("Invalid token type");}
        }
    }
}
