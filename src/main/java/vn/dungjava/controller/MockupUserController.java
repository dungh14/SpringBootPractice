package vn.dungjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.dungjava.common.Gender;
import vn.dungjava.controller.request.UserCreationRequest;
import vn.dungjava.controller.request.UserPasswordRequest;
import vn.dungjava.controller.request.UserUpdateRequest;
import vn.dungjava.controller.response.UserResponse;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mockup/user")
@Tag(name = "Mockup User Controller")
public class MockupUserController {

    @Operation(summary = "Get user list", description = "API retrieve user from db")
    @GetMapping("/list")
    public  Map<String, Object> getList(@RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1L);
        userResponse1.setFirstName("dung");
        userResponse1.setLastName("hoang");
        userResponse1.setGender(Gender.MALE);
        userResponse1.setDateOfBirth(new Date());
        userResponse1.setUsername("admin");
        userResponse1.setEmail("admin@gmail.com");
        userResponse1.setPhone("091234567");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2L);
        userResponse2.setFirstName("kien");
        userResponse2.setLastName("duc");
        userResponse2.setGender(Gender.MALE);
        userResponse2.setDateOfBirth(new Date());
        userResponse2.setUsername("user");
        userResponse2.setEmail("user@gmail.com");
        userResponse2.setPhone("0987654321");
        List<UserResponse> userList = List.of(userResponse1, userResponse2);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userList);

        return result;
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by id")
    @GetMapping("/{userId}")
    public  Map<String, Object> getUserDetail(@PathVariable @Min(1) Long userId) {

        UserResponse userDetail = new UserResponse();
        userDetail.setId(1L);
        userDetail.setFirstName("dung");
        userDetail.setLastName("hoang");
        userDetail.setGender(Gender.MALE);
        userDetail.setDateOfBirth(new Date());
        userDetail.setUsername("admin");
        userDetail.setEmail("admin@gmail.com");
        userDetail.setPhone("091234567");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user");
        result.put("data", userDetail);

        return result;
    }

    @Operation(summary = "Create user", description = "API add new user to db")
    @PostMapping("/add")
    public  Map<String, Object> createUser(UserCreationRequest request) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User created successfully");
        result.put("data", 3);

        return result;
    }

    @Operation(summary = "Update user", description = "API update user")
    @PutMapping("/{user}")
    public  Map<String, Object> updateUser(UserUpdateRequest request) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User updated successfully");
        result.put("data", "");

        return result;
    }

    @Operation(summary = "Change password", description = "API update password for user")
    @PatchMapping("/change-pwd")
    public  Map<String, Object> changePassword(UserPasswordRequest request) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "Password updated successfully");
        result.put("data", "");

        return result;
    }

    @Operation(summary = "Delete user", description = "API inactivate user")
    @DeleteMapping("/del/{userId}")
    public  Map<String, Object> deleteUser(@PathVariable @Min(1) Long userId) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "User deleted successfully");
        result.put("data", "");

        return result;
    }
}
