package com.dineshb.projects.usermgmt.portal.filters;

import com.dineshb.projects.usermgmt.portal.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.OPTIONS;
import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("MSG='Inside JwtAuthorizationFilter', request={}, method={}", request.getRequestURI(), request.getMethod());
        if (request.getMethod().equalsIgnoreCase(OPTIONS)) {
            response.setStatus(OK.value());
        } else {
            // Check for 'Authorization' header in request..
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwtToken = authorizationHeader.substring(TOKEN_PREFIX.length());
            String userName = jwtTokenProvider.getSubjectFromToken(jwtToken);
            // Now, check whether this token is valid (or) not !!
            if (jwtTokenProvider.isValidToken(userName, jwtToken) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = jwtTokenProvider.getClaimsFromToken(jwtToken);
                Authentication authentication = jwtTokenProvider.getAuthentication(userName, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Clear the SecurityContext..
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
