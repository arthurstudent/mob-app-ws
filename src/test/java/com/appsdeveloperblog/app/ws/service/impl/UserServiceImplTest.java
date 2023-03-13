package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.RoleRepository;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.shared.utils.AmazonSES;
import com.appsdeveloperblog.app.ws.shared.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    AmazonSES amazonSES;

    @InjectMocks
    UserServiceImpl userService;

    UserEntity user;

    RoleEntity role;
    static final String userId = "11111111";
    static final String encryptedPassword = "$2a$10$qyJcY5w/123iIiEyLzAjVOQNIGJ3D7ji7eR/V0RuwIWB6GIUj8Xa";
    static final String firstName = "Van";
    static final String lastName = "Dorn";
    static final String emailVerificationToken = "asjhad43534GHjlkKGLhjkhjk";
    static final String email = "test@gmail.com";

    static final Set<RoleEntity> roles = new HashSet<>();

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        role = new RoleEntity();

        user.setId(1L);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserId(userId);
        user.setEncryptedPassword(encryptedPassword);
        user.setEmailVerificationToken(emailVerificationToken);
        user.setEmail(email);
        user.setAddresses(getAddressesEntity());
        user.setRoles(List.of(role));

        role.setId(1L);
        role.setName("ROLE_USER");
        role.setUsers(List.of(user));

        roles.add(role);
    }

    @Test
    final void testGetUser() {

        when(userRepository.findUserByEmail(anyString())).thenReturn(user);

        UserDto userDto = userService.getUser(email);
        assertNotNull(userDto);
        assertEquals(firstName, userDto.getFirstName());
    }

    @Test
    final void testGetUser_userNameNotFound() {

        when(userRepository.findUserByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser(email));
    }

    @Test
    final void testCreateUser_CreateUserServiceException() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(user);
        UserDto userDto = new UserDto();
        userDto.setEmail(email);

        assertThrows(UserServiceException.class, () -> userService.createUser(userDto));
    }

    @Test
    final void testCreateUser() {

        when(userRepository.findUserByEmail(anyString())).thenReturn(null);
        when(utils.generateId()).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(roleRepository.findAllRoles(any())).thenReturn(roles);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setPassword("12345678");
        userDto.setEmail(email);

        UserDto returnedUserDto = userService.createUser(userDto);

        assertNotNull(returnedUserDto);
        assertEquals(user.getFirstName(), returnedUserDto.getFirstName());
        assertEquals(user.getLastName(), returnedUserDto.getLastName());
        assertNotNull(returnedUserDto.getUserId());
        assertEquals(returnedUserDto.getAddresses().size(), user.getAddressEntityList().size());
        verify(utils, times(3)).generateId();
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testGetUserById_notFound() {
        String exceptedMessage = "Record with provided id is not found";
        when(userRepository.findByUserId(anyString())).thenReturn(null);

        String exceptionMessage = assertThrows(UserServiceException.class,
                () -> userService.getUserByUserId(userId)).getMessage();
        assertEquals(exceptedMessage, exceptionMessage);
    }

    @Test
    void testGetUserById() {
        when(userRepository.findByUserId(anyString())).thenReturn(user);
        UserDto userById = userService.getUserByUserId(userId);

        assertNotNull(userById);
        assertEquals(userId, userById.getUserId());
    }

    private List<AddressDTO> getAddressesDto() {
        AddressDTO addressDto = new AddressDTO();
        addressDto.setType("shipping");
        addressDto.setCity("Vancouver");
        addressDto.setCountry("Canada");
        addressDto.setPostalCode("ABC123");
        addressDto.setStreetName("123 Street name");

        AddressDTO billingAddressDto = new AddressDTO();
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Vancouver");
        billingAddressDto.setCountry("Canada");
        billingAddressDto.setPostalCode("ABC123");
        billingAddressDto.setStreetName("123 Street name");

        List<AddressDTO> addresses = new ArrayList<>();
        addresses.add(addressDto);
        addresses.add(billingAddressDto);

        return addresses;
    }

    private List<AddressEntity> getAddressesEntity() {
        return new ModelMapper().map(getAddressesDto(),
                new TypeToken<List<AddressEntity>>() {
                }.getType());
    }
}
