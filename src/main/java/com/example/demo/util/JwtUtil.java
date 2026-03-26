package com.example.demo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // ✅ Hardcode a strong secret directly — no @Value on static fields
    private static final String SECRET = "V7rK9mP2xQ4wZ6nL8sY1uA3dF5hJ0cEbRv2TqXwPnMs";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ✅ Generate token
    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract email
    public static String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static String extractTokenFromHeader(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}