package vn.dungjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.dungjava.common.Gender;
import vn.dungjava.controller.request.UserCreationRequest;
import vn.dungjava.controller.request.UserPasswordRequest;
import vn.dungjava.controller.request.UserUpdateRequest;
import vn.dungjava.controller.response.ApiResponse;
import vn.dungjava.controller.response.UserPageResponse;
import vn.dungjava.controller.response.UserResponse;
import vn.dungjava.service.UserService;
import vn.dungjava.service.impl.UserServiceImpl;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@Slf4j(topic = "USER_CONTROLLER")
@RequiredArgsConstructor
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get user list", description = "API retrieve user from db")
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
//    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ApiResponse getList(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        log.info("Get user list");

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user")
                .data(userService.findAll(keyword, sort, page, size))
                .build();
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by id")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user')")
    public ApiResponse getUserDetail(@PathVariable @Min(value = 1, message = "userId must be equal or greater than 1") Long userId) {

        log.info("Get user detail by id: {}", userId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user list")
                .data(userService.findById(userId))
                .build();
    }

    @Operation(summary = "Create user", description = "API add new user to db")
    @PostMapping("/add")
    public ApiResponse createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create user: {}", request);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(userService.save(request))
                .build();
    }

    @Operation(summary = "Update user", description = "API update user")
    @PutMapping("/{user}")
    public ApiResponse updateUser(@RequestBody @Valid UserUpdateRequest request) {

        userService.update(request);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("User updated successfully")
                .build();
    }

    @Operation(summary = "Change password", description = "API update password for user")
    @PatchMapping("/change-pwd")
    public ApiResponse changePassword(@RequestBody @Valid UserPasswordRequest request) {
        log.info("Change password for user: {}", request);

        userService.changePassword(request);

        return ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Password updated successfully")
                .build();
    }

    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam("email") String secretCode, HttpServletResponse response) throws IOException {
        log.info("Confirm email: {}", secretCode);
        try {
            //check or compare secretCode from db

        } catch (Exception e) {
            log.error("Confirm email failed: {}", e.getMessage());
        } finally {
            response.sendRedirect("https://tayjava.vn/wp-admin");
        }
    }

    @Operation(summary = "Delete user", description = "API inactivate user")
    @DeleteMapping("/del/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public ApiResponse deleteUser(@PathVariable @Min(value = 1, message = "userId must be equal or greater than 1") Long userId) {
        log.info("Delete user: {}", userId);

        userService.delete(userId);

        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("User deleted successfully")
                .build();
    }
}
