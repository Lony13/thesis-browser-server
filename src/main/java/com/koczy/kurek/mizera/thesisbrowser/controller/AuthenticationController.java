package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.config.TokenProvider;
import com.koczy.kurek.mizera.thesisbrowser.model.LoginUser;
import com.koczy.kurek.mizera.thesisbrowser.service.IAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/token")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private TokenProvider jwtTokenUtil;
    private IAuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    TokenProvider jwtTokenUtil,
                                    IAuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationService = authenticationService;
    }


    @RequestMapping(value = "/generate-token", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody LoginUser loginUser) throws AuthenticationException {
        return authenticationService.register(loginUser, jwtTokenUtil, authenticationManager);
    }

}