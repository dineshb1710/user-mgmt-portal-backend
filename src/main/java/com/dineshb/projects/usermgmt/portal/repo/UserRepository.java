package com.dineshb.projects.usermgmt.portal.repo;

import com.dineshb.projects.usermgmt.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(final String username);

    Optional<User> findUserByEmail(final String email);
}
