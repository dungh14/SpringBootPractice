package vn.dungjava.service;

import org.springframework.stereotype.Service;
import vn.dungjava.controller.request.SignInRequest;
import vn.dungjava.controller.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse getAccessToken(SignInRequest request);

    TokenResponse getRefreshToken(String request);
}
