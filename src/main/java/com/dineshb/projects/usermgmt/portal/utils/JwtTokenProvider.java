package com.dineshb.projects.usermgmt.portal.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dineshb.projects.usermgmt.portal.model.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.*;

public class JwtTokenProvider {

    @Value("${jwt-config.secret}")
    private String jwtSecret;

    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = fetchClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withArrayClaim(AUTHORITIES, claims)
                .withSubject(userPrincipal.getUsername())
                .sign(Algorithm.HMAC512(jwtSecret.getBytes()));
    }

    private String[] fetchClaimsFromUser(UserPrincipal userPrincipal) {
        return null;
    }


}
