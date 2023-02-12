package com.appsdeveloperblog.app.ws.ui;

import com.appsdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.controller.UserController;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class UserControllerTest {
    @InjectMocks
    UserController userController;
    @Mock
    UserServiceImpl userService;

    UserDto userDto;

    static final String userId = "11111111";
    static final String encryptedPassword = "$2a$10$qyJcY5w/123iIiEyLzAjVOQNIGJ3D7ji7eR/V0RuwIWB6GIUj8Xa";
    static final String firstName = "Van";
    static final String lastName = "Dorn";
    static final String emailVerificationToken = "asjhad43534GHjlkKGLhjkhjk";
    static final String email = "test@gmail.com";

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setUserId(userId);
        userDto.setEncryptedPassword(encryptedPassword);
        userDto.setEmailVerificationToken(null);
        userDto.setEmailVerificationStatus(false);
        userDto.setEmail(email);
        userDto.setAddresses(getAddressesDto());
    }

    @Test
    final void testGetUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);
        UserRest user = userController.getUser(userId);

        assertNotNull(user);
        assertEquals(userId, user.getUserId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        // fix method
//        assertEquals(2, user.getAddressesRestList().size());
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
}
