package com.dineshb.projects.usermgmt.portal.listener;

import com.dineshb.projects.usermgmt.portal.cache.UserLoginCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener {

    private final UserLoginCacheService userLoginCacheService;

    @EventListener
    public void onAuthenticationFailureEvent(AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent) throws ExecutionException {
        log.info("MSG='Authentication Failure Detected', source={}", authenticationFailureBadCredentialsEvent.getSource());
        Object authenticationPrincipal = authenticationFailureBadCredentialsEvent.getAuthentication().getPrincipal();
        if (authenticationPrincipal instanceof String) {
            String username = (String) authenticationPrincipal;
            userLoginCacheService.addInvalidLoginAttemptToCache(username);
        }
    }
}
