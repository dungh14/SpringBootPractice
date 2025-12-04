package vn.dungjava.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import vn.dungjava.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceDetail {

    private final UserRepository userRepository;

    public UserDetailsService UserServiceDetail() {
        return userRepository::findByUsername;
    }
}
