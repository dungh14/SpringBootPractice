package vn.dungjava.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.dungjava.common.Gender;
import vn.dungjava.common.UserStatus;
import vn.dungjava.common.UserType;
import vn.dungjava.controller.request.AddressRequest;
import vn.dungjava.controller.request.UserCreationRequest;
import vn.dungjava.controller.request.UserUpdateRequest;
import vn.dungjava.controller.response.UserPageResponse;
import vn.dungjava.controller.response.UserResponse;
import vn.dungjava.exception.ResourceNotFoundException;
import vn.dungjava.model.UserEntity;
import vn.dungjava.repository.AddressRepository;
import vn.dungjava.repository.UserRepository;
import vn.dungjava.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    private @Mock UserRepository userRepository;
    private @Mock AddressRepository addressRepository;
    private @Mock PasswordEncoder passwordEncoder;
    private @Mock EmailService emailService;

    private static UserEntity dungJava;
    private static UserEntity johnDoe;

    @BeforeAll
    static void beforeAll() {
        dungJava = new UserEntity();
        dungJava.setId(1L);
        dungJava.setFirstName("Dung");
        dungJava.setLastName("Java");
        dungJava.setGender(Gender.MALE);
        dungJava.setDateOfBirth(new Date());
        dungJava.setEmail("hoangdung1412003@gmail.com");
        dungJava.setPhone("0912345678");
        dungJava.setPassword("password");
        dungJava.setType(UserType.USER);
        dungJava.setStatus(UserStatus.ACTIVE);

        johnDoe = new UserEntity();
        dungJava.setId(2L);
        dungJava.setFirstName("John");
        dungJava.setLastName("Doe");
        dungJava.setGender(Gender.FEMALE);
        dungJava.setDateOfBirth(new Date());
        dungJava.setEmail("johndoe@gmail.com");
        dungJava.setPhone("0912345528");
        dungJava.setPassword("password");
        dungJava.setType(UserType.USER);
        dungJava.setStatus(UserStatus.INACTIVE);
    }

    @BeforeEach
    void setUp() {
        // khoi tao buoc trien khai la UserService
        userService = new UserServiceImpl(userRepository, addressRepository, passwordEncoder, emailService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetListUsers_Success() {
        //gia lap phuong thuc
        Page<UserEntity> usePage = new PageImpl<>(Arrays.asList(dungJava, johnDoe));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usePage);

        //goi phuong thuc can test
        UserPageResponse result = userService.findAll(null, null, 0, 20);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testSearchUser_Success() {
        //gia lap phuong thuc
        Page<UserEntity> usePage = new PageImpl<>(Arrays.asList(dungJava, johnDoe));
        when(userRepository.searchByKeywords(any(), any(Pageable.class))).thenReturn(usePage);

        //goi phuong thuc can test
        UserPageResponse result = userService.findAll("Dung", null, 0, 20);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testGetListUsers_Empty() {
        //gia lap phuong thuc
        Page<UserEntity> usePage = new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usePage);

        //goi phuong thuc can test
        UserPageResponse result = userService.findAll(null, null, 0, 20);
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testGetUserById_Success() {
        //gia lap phuong thuc
        when(userRepository.findById(1L)).thenReturn(Optional.of(dungJava));

        UserResponse userResult = userService.findById(1L);
        assertNotNull(userResult);
        assertEquals(1L, userResult.getId());
    }

    @Test
    void testGetUserById_Failure() {
        //gia lap phuong thuc
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findByUsername() {

    }

    @Test
    void findByEmail() {
    }

    @Test
    void testSaveUser_Success() {
        //gia lap phuong thuc
        when(userRepository.save(any(UserEntity.class))).thenReturn(dungJava);

        //tao request
        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setFirstName("Dung");
        userCreationRequest.setLastName("Java");
        userCreationRequest.setGender(Gender.MALE);
        userCreationRequest.setDateOfBirth(new Date());
        userCreationRequest.setEmail("hoangdung1412003@gmail.com");
        userCreationRequest.setPhone("0912345678");
        userCreationRequest.setUsername("Dung");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setApartmentNumber("Apartment number");
        addressRequest.setFloor("Floor");
        addressRequest.setBuilding("Building");
        addressRequest.setStreetNumber("Street number");
        addressRequest.setStreet("Street");
        addressRequest.setCity("City");
        addressRequest.setCountry("Country");
        addressRequest.setAddressType(1);
        userCreationRequest.setAddress(List.of(addressRequest));

        long userId = userService.save(userCreationRequest);
        assertEquals(2L, userId);
    }

    @Test
    void testUpdateUser_Success() {
        Long userId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFirstName("Jane");
        userEntity.setLastName("Smith");
        userEntity.setGender(Gender.FEMALE);
        userEntity.setDateOfBirth(new Date());
        userEntity.setEmail("janesmith@gmail.com");
        userEntity.setPhone("0912345528");
        userEntity.setUsername("janesmith");
        userEntity.setPassword("password");
        userEntity.setType(UserType.USER);
        userEntity.setStatus(UserStatus.ACTIVE);

        //gia lap hanh vi cua UserRepository
        when(userRepository.findById(userId)).thenReturn(Optional.of(johnDoe));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setId(userId);
        userUpdateRequest.setFirstName("Jane");
        userUpdateRequest.setLastName("Smith");
        userUpdateRequest.setGender(Gender.FEMALE);
        userUpdateRequest.setDateOfBirth(new Date());
        userUpdateRequest.setEmail("janesmith@gmail.com");
        userUpdateRequest.setPhone("0912345528");
        userUpdateRequest.setUsername("janesmith");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setApartmentNumber("Apartment number");
        addressRequest.setFloor("Floor");
        addressRequest.setBuilding("Building");
        addressRequest.setStreetNumber("Street number");
        addressRequest.setStreet("Street");
        addressRequest.setCity("City");
        addressRequest.setCountry("Country");
        addressRequest.setAddressType(1);

        userUpdateRequest.setAddresses(List.of(addressRequest));

        userService.update(userUpdateRequest);

        UserResponse result = userService.findById(userId);
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void changePassword() {

    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(dungJava));
        userService.delete(userId);
        assertEquals(UserStatus.INACTIVE, dungJava.getStatus());
        verify(userRepository, times(1)).save(dungJava);
    }
}