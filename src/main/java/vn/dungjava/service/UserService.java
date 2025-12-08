package vn.dungjava.service;

import vn.dungjava.controller.request.UserCreationRequest;
import vn.dungjava.controller.request.UserPasswordRequest;
import vn.dungjava.controller.request.UserUpdateRequest;
import vn.dungjava.controller.response.UserPageResponse;
import vn.dungjava.controller.response.UserResponse;

public interface UserService {

    UserPageResponse findAll(String keyword, String sort, int page, int pageSize);

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    UserResponse findByEmail(String email);

    long save(UserCreationRequest req);

    void update(UserUpdateRequest req);

    void changePassword(UserPasswordRequest req);

    void delete(Long id);
}
