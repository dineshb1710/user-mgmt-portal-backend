package com.dineshb.projects.usermgmt.portal.repo;

import com.dineshb.projects.usermgmt.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(final String username);

    User findUserByEmail(final String email);
}
