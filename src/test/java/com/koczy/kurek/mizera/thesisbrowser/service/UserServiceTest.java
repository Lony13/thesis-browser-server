package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Role;
import com.koczy.kurek.mizera.thesisbrowser.entity.User;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;
    private BCryptPasswordEncoder bcryptEncoder;
    private UserDao userDao;

    private UserDetails userDetails;
    private User user;
    private String username = "test user";
    private String password = "password";
    private String invalidName = "invalid name";
    private Set<Role> roles;
    private Set<SimpleGrantedAuthority> simpleGrantedAuthorities;

    @Before
    public void setUp() {
        roles = new HashSet<>();
        roles.add(new Role(1, "USER", ""));

        user = new User();
        user.setUsername(username);
        user.setRoles(roles);
        user.setPassword(password);

        simpleGrantedAuthorities = new HashSet<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                simpleGrantedAuthorities);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername() {
        bcryptEncoder = mock(BCryptPasswordEncoder.class);
        userDao = mock(UserDao.class);
        userService = new UserService(userDao, bcryptEncoder);

        when(userDao.findByUsername(username)).thenReturn(user);

        UserDetails userDetailsTest = userService.loadUserByUsername(username);
        assertEquals(userDetails, userDetailsTest);

        when(userDao.findByUsername(invalidName)).thenReturn(null);

        userService.loadUserByUsername(invalidName);
    }
}