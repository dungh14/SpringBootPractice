package vn.dungjava.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import vn.dungjava.common.Gender;
import vn.dungjava.controller.response.UserPageResponse;
import vn.dungjava.controller.response.UserResponse;
import vn.dungjava.service.JwtService;
import vn.dungjava.service.UserService;
import vn.dungjava.service.UserServiceDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserServiceDetail userServiceDetail;

    @MockBean
    private JwtService jwtService;

    private static UserResponse dungJava;
    private static UserResponse johnDoe;

    @BeforeAll
    static void setup() {
        //Chuan bi du lieu
        dungJava = new UserResponse();
        dungJava.setId(1L);
        dungJava.setFirstName("Dung");
        dungJava.setLastName("Java");
        dungJava.setGender(Gender.MALE);
        dungJava.setDateOfBirth(new Date());
        dungJava.setEmail("hoangdung1412003@gmail.com");
        dungJava.setPhone("0912345678");
        dungJava.setUsername("dungjava");

        johnDoe = new UserResponse();
        johnDoe.setId(2L);
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        johnDoe.setGender(Gender.FEMALE);
        johnDoe.setDateOfBirth(new Date());
        johnDoe.setEmail("johndoe@gmail.com");
        johnDoe.setPhone("0912345678");
        johnDoe.setUsername("johndoe");
    }

    @Test
    @WithMockUser(authorities = {"admin", "manager"})
    void testGetUser() throws Exception {
        List<UserResponse> userResponses = List.of(dungJava, johnDoe);

        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.setPageNumber(0);
        userPageResponse.setPageSize(20);
        userPageResponse.setTotalPages(1);
        userPageResponse.setTotalElements(2);
        userPageResponse.setUsers(userResponses);

        when(userService.findAll(null, null, 0, 20)).thenReturn(userPageResponse);

        mockMvc.perform(get("/user/list").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.status", is(200)))
//                .andExpect(jsonPath("$.message", is("users")))
//                .andExpect(jsonPath("$.data.totalElements", is(2)));
    }
}
