package com.dineshb.projects.usermgmt.portal.listener;

import com.dineshb.projects.usermgmt.portal.cache.UserLoginCacheService;
import com.dineshb.projects.usermgmt.portal.model.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {

    private final UserLoginCacheService userLoginCacheService;

    @EventListener
    public void onAuthenticationSuccessEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
        log.info("MSG='Authentication Success Detected', source={}", authenticationSuccessEvent.getSource());
        Object authenticationPrincipal = authenticationSuccessEvent.getAuthentication().getPrincipal();
        if (authenticationPrincipal instanceof UserPrincipal) {
            UserPrincipal loggedInUser = (UserPrincipal) authenticationPrincipal;
            userLoginCacheService.invalidateUserFromCache(loggedInUser.getUsername());
        }
    }
}
