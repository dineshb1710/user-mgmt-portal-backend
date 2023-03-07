package com.dineshb.projects.usermgmt.portal.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.dineshb.projects.usermgmt.portal.model.security.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.ISSUER;
import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.AUDIENCE;
import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.AUTHORITIES;
import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.CANNOT_VERIFY_TOKEN;
import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.EXPIRATION_TIME;
import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.TOKEN_EXPIRED;

@Component
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
        return userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()).toArray(new String[0]);
    }

    public List<GrantedAuthority> getClaimsFromToken(String token) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        try {
            JWTVerifier jwtVerifier = getVerifier();
            grantedAuthorities = Arrays.stream(jwtVerifier.verify(token)
                    .getClaim(AUTHORITIES).asArray(String.class))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (JWTVerificationException e) {
            new JWTVerificationException(CANNOT_VERIFY_TOKEN);
        }
        return grantedAuthorities;
    }

    private JWTVerifier getVerifier() {
        return JWT.require(Algorithm.HMAC512(jwtSecret))
                .withIssuer(ISSUER).build();
    }

    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest httpServletRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isValidToken(String username, String token) {
        return StringUtils.isNotEmpty(username) && !isTokenExpired(token);
    }

    public String getSubjectFromToken(String token) {
        try {
            return getVerifier()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(CANNOT_VERIFY_TOKEN);
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return getVerifier()
                    .verify(token)
                    .getExpiresAt()
                    .before(new Date());
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(TOKEN_EXPIRED);
        }
    }
}
