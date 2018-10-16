package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.config.TokenProvider;
import com.koczy.kurek.mizera.thesisbrowser.model.AuthToken;
import com.koczy.kurek.mizera.thesisbrowser.model.LoginUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

public interface IAuthenticationService {

    ResponseEntity<AuthToken> register(LoginUser loginUser,
                                       TokenProvider jwtTokenUtil,
                                       AuthenticationManager authenticationManager);

}
