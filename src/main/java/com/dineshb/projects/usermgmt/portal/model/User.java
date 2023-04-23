package com.dineshb.projects.usermgmt.portal.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * User Model.
 */

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String profileImageUrl;

    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joiningDate;

    private String role;
    private String[] authorities;

    private boolean isActive;
    private boolean isLocked;
}
