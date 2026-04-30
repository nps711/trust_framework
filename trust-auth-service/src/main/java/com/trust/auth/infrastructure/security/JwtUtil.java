package com.trust.auth.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${trust.auth.jwt.secret}")
    private String secret;

    @Value("${trust.auth.jwt.access-token-expire:7200000}")
    private long accessTokenExpire;

    public String generateToken(String userId, String username, Long deptId) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + accessTokenExpire);
        return JWT.create()
                .withSubject(userId)
                .withClaim("username", username)
                .withClaim("deptId", deptId)
                .withIssuedAt(now)
                .withExpiresAt(expire)
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
    }

    public String extractUserId(String token) {
        return verify(token).getSubject();
    }
}
