package com.dineshb.projects.usermgmt.portal.service.impl;

import com.dineshb.projects.usermgmt.portal.model.User;
import com.dineshb.projects.usermgmt.portal.model.security.UserPrincipal;
import com.dineshb.projects.usermgmt.portal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        log.info("MSG='Trying to find user with name', username={}", username);
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (!optionalUser.isPresent()) {
            log.error("MSG='User with name {} doesn't exist !!'", username);
            throw new UsernameNotFoundException("User with name " + username + " doesn't exist !!'");
        }
        // Update user with login details..
        User user = optionalUser.get();
        user.setLastLoginDate(new Date());
        userRepository.save(user);

        // Build UserDetails object & return..
        return new UserPrincipal(user);
    }
}
