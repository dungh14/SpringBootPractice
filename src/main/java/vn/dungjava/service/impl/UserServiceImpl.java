package vn.dungjava.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.dungjava.common.UserStatus;
import vn.dungjava.controller.request.UserCreationRequest;
import vn.dungjava.controller.request.UserPasswordRequest;
import vn.dungjava.controller.request.UserUpdateRequest;
import vn.dungjava.controller.response.UserPageResponse;
import vn.dungjava.controller.response.UserResponse;
import vn.dungjava.exception.InvalidDataException;
import vn.dungjava.exception.ResourceNotFoundException;
import vn.dungjava.model.AddressEntity;
import vn.dungjava.model.UserEntity;
import vn.dungjava.repository.AddressRepository;
import vn.dungjava.repository.UserRepository;
import vn.dungjava.service.EmailService;
import vn.dungjava.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    @Override
    public UserPageResponse findAll(String keyword, String sort, int page, int pageSize) {

        //Sorting
        Sort.Order order =  new Sort.Order(Sort.Direction.DESC, "id");
        if(StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); //tencot:asc|desc
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()) {
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")) {
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }

        //Xu ly truong hop FE muon trang bat dau voi page = 1
        int pageNo = 0;
        if(page > 0) {
            pageNo = page - 1;
        }

        //Paging
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(order));

        Page<UserEntity> entityPage;

        if(StringUtils.hasLength(keyword)) {
            //goi search method
            keyword = "%" + keyword.toLowerCase() + "%";
            entityPage = userRepository.searchByKeywords(keyword, pageable);

        } else {
            entityPage = userRepository.findAll(pageable);
        }

        UserPageResponse response = getUserPageResponse(page, pageSize, entityPage);

        return response;
    }


    @Override
    public UserResponse findById(Long id) {
        UserEntity userEntity = getUserEntity(id);

        return UserResponse.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .dateOfBirth(userEntity.getDateOfBirth())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .build();
    }

    @Override
    public UserResponse findByUsername(String username) {
        return null;
    }

    @Override
    public UserResponse findByEmail(String email) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(UserCreationRequest req) {
        log.info("Save user: {}", req);

        UserEntity userByEmail = userRepository.findByEmail(req.getEmail());
        if(userByEmail != null) {
            throw new InvalidDataException("Email already exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(req.getFirstName());
        userEntity.setLastName(req.getLastName());
        userEntity.setGender(req.getGender());
        userEntity.setDateOfBirth(req.getDateOfBirth());
        userEntity.setEmail(req.getEmail());
        userEntity.setPhone(req.getPhone());
        userEntity.setUsername(req.getUsername());
        userEntity.setType(req.getType());
        userEntity.setStatus(UserStatus.NONE);

        userRepository.save(userEntity);
        log.info("Saved user: {}", userEntity);

        if(userEntity.getId() != null) {
            log.info("User with id: {}", userEntity.getId());

            List<AddressEntity> addresses = new ArrayList<>();
            req.getAddress().forEach(address -> {
                AddressEntity addressEntity = new AddressEntity();
                addressEntity.setApartmentNumber(address.getApartmentNumber());
                addressEntity.setFloor(address.getFloor());
                addressEntity.setBuilding(address.getBuilding());
                addressEntity.setStreetNumber(address.getStreetNumber());
                addressEntity.setStreet(address.getStreet());
                addressEntity.setCity(address.getCity());
                addressEntity.setCountry(address.getCountry());
                addressEntity.setAddressType(address.getAddressType());
                addressEntity.setUserId(userEntity.getId());
                addresses.add(addressEntity);
            });
            addressRepository.saveAll(addresses);
            log.info("Save address: {}", addresses);
        }

        //send email confirm
        try {
            emailService.emailVerification(req.getEmail(), req.getUsername());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return userEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("Update user: {}", req);
        UserEntity user = getUserEntity(req.getId());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());

        userRepository.save(user);
        log.info("Updated user: {}", user);

        //save address
        List<AddressEntity> addresses = new ArrayList<>();

        req.getAddresses().forEach(address -> {
            AddressEntity addressEntity = addressRepository.findByUserIdAndAddressType(user.getId(), address.getAddressType());
            if(addressEntity == null) {
                addressEntity = new AddressEntity();
            }
            addressEntity.setApartmentNumber(address.getApartmentNumber());
            addressEntity.setFloor(address.getFloor());
            addressEntity.setBuilding(address.getBuilding());
            addressEntity.setStreetNumber(address.getStreetNumber());
            addressEntity.setStreet(address.getStreet());
            addressEntity.setCity(address.getCity());
            addressEntity.setCountry(address.getCountry());
            addressEntity.setAddressType(address.getAddressType());
            addressEntity.setUserId(user.getId());

            addresses.add(addressEntity);
        });

        //save address
        addressRepository.saveAll(addresses);
        log.info("Updated address: {}", addresses);

    }

    @Override
    public void changePassword(UserPasswordRequest req) {
        log.info("Change password for user: {}", req);
        UserEntity user = getUserEntity(req.getId());

        if(req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        userRepository.save(user);
        log.info("Changed password user: {}", user);
    }

    @Override
    public void delete(Long id) {
        log.info("Change password for user: {}", id);

        UserEntity user = getUserEntity(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("Deleted user: {}", user);
    }

    private UserEntity getUserEntity(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Convert userEntities to userResponse
     * @param page
     * @param pageSize
     * @param userEntities
     * @return
     */
    private static UserPageResponse getUserPageResponse(int page, int pageSize, Page<UserEntity> userEntities) {
        List<UserResponse> userList = userEntities.stream().map(
                entity -> new UserResponse().builder()
                        .id(entity.getId())
                        .firstName(entity.getFirstName())
                        .lastName(entity.getLastName())
                        .gender(entity.getGender())
                        .dateOfBirth(entity.getDateOfBirth())
                        .username(entity.getUsername())
                        .email(entity.getEmail())
                        .phone(entity.getPhone())
                        .build()
        ).toList();

        UserPageResponse response = new UserPageResponse();
        response.setPageNumber(page);
        response.setPageSize(pageSize);
        response.setTotalElements(userEntities.getTotalElements());
        response.setTotalPages(userEntities.getTotalPages());
        response.setUsers(userList);
        return response;
    }
}
