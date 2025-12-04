package vn.dungjava.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.dungjava.controller.request.SignInRequest;
import vn.dungjava.controller.response.TokenResponse;
import vn.dungjava.repository.UserRepository;
import vn.dungjava.service.AuthenticationService;
import vn.dungjava.service.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get access token");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Login failed!, message: {}",e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }

        var user =  userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), request.getUsername(), user.getAuthorities());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), request.getUsername(), user.getAuthorities());

        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    @Override
    public TokenResponse getRefreshToken(String request) {
        return null;
    }
}
