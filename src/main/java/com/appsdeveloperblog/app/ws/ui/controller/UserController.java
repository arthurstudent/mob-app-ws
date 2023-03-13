package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.shared.utils.Roles;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetModel;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressesRest;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    final UserService userService;
    final AddressService addressService;

    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @ApiOperation(value = "The Get User Details Web Service Endpoint",
            notes = "This Web Service Endpoint returns User Details. Use public user id in an URI path. For example: users/1122334455")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @PostAuthorize("hasRole('ADMIN') or returnObject.userId == principal.publicUserId")
    @GetMapping(path = "/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable String userId) {
        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(userId);

        ModelMapper modelMapper = new ModelMapper();
        returnValue = modelMapper.map(userDto, UserRest.class);
        returnValue.setAddresses(modelMapper
                .map(userDto.getAddresses(), new TypeToken<List<AddressesRest>>() {
                }.getType()));

        return returnValue;
    }

    @ApiOperation(value = "The Get Users Details Web Service Endpoint",
            notes = "This Web Service Endpoint returns Users Details")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> restList = new ArrayList<>();
        List<UserDto> users = userService.getListUsers(page, limit);
        for (UserDto userDto : users) {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto, userRest);
            restList.add(userRest);
        }
        return restList;
    }

    @ApiOperation(value = "The Get all User's addresses Web Service Endpoint",
            notes = "This Web Service Endpoint returns all user's addresses. Use public user id in an URI path. For example: users/1122334455/addresses")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == principal.publicUserId")
    @GetMapping(path = "/{userId}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String userId) {

        List<AddressesRest> addressesRestList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        List<AddressDTO> addressDTOS = addressService.getAddresses(userId);

        if (addressDTOS != null && !addressDTOS.isEmpty()) {
            addressesRestList = modelMapper.map(addressDTOS, new TypeToken<List<AddressesRest>>() {
            }.getType());

            for (AddressesRest addressRest : addressesRestList) {
                Link selfLink = WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressRest.getAddressId()))
                        .withSelfRel();
                addressRest.add(selfLink);
            }
        }
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                .withSelfRel();
        return CollectionModel.of(addressesRestList, userLink, selfLink);
    }

    @ApiOperation(value = "The Get User's address Web Service Endpoint",
            notes = "This Web Service Endpoint returns user's address. Use public user id in an URI path. " +
                    "For example: users/1122334455/address/1122334455")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == principal.publicUserId")
    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDTO addressDTO = addressService.getAddress(addressId);
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        Link userAddressesLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                .withRel("addresses");
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
                .withSelfRel();

        AddressesRest addressesRest = new ModelMapper().map(addressDTO, AddressesRest.class);

        return EntityModel.of(addressesRest, Arrays.asList(userLink, userAddressesLink, selfLink));
    }

    @ApiOperation(value = "Verify email Web Service Endpoint",
            notes = "This Web Service Endpoint returns status of email verification. " +
                    "Use token from email in an URI path. " +
                    "For example: users/email-verification?token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5NDY3Mzc2NTUwIiwiZXhwIjoxNjc2Mjk5MjIyfQ.Sz935Mf7OzTwRhs3TZLPyQ2ucFjoU2eJxCKFIH3pNcjEtt9qvYYfNXjJ3oI4hyvCpORXmhJ_quiiXLdzlHdaKQ")
    @GetMapping(path = "/email-verification",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatusModel operationStatusModel = new OperationStatusModel();
        operationStatusModel.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);
        operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        if (isVerified) {
            operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            operationStatusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        }
        return operationStatusModel;
    }

    @ApiOperation(value = "The Post User Web Service Endpoint",
            notes = "This Web Service Endpoint create user. Use Json or xml type to provide required fields")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue;
//        if(userDetails.getFirstName().isEmpty())
//            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(userDetails, userDto);

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        userDto.setRoles(new HashSet<>(List.of(Roles.ROLE_USER.name())));

        UserDto createdUser = userService.createUser(userDto);
        returnValue = modelMapper.map(createdUser, UserRest.class);
        returnValue.setAddresses(modelMapper
                .map(createdUser.getAddresses(), new TypeToken<List<AddressesRest>>() {
                }.getType()));
        return returnValue;
    }

    @ApiOperation(value = "The Post password reset Web Service Endpoint",
            notes = "This Web Service Endpoint uses for sending password reset request. Use Json or xml type to provide required fields")
    @PostMapping(path = "/password-reset-request", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel passwordReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel operationStatusModel = new OperationStatusModel();
        operationStatusModel.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isPasswordResetRequestSent = userService.passwordResetRequest(passwordResetRequestModel.getEmail());

        operationStatusModel.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        operationStatusModel.setOperationResult(RequestOperationStatus.ERROR.name());

        if (isPasswordResetRequestSent) {
            operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return operationStatusModel;
    }

    @ApiOperation(value = "The Post Web Service Endpoint",
            notes = "This Web Service Endpoint uses to reset password. Use Json or xml type to provide required fields")
    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean isPasswordUpdated = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (isPasswordUpdated) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

    @ApiOperation(value = "The Put User Web Service Endpoint",
            notes = "This Web Service Endpoint uses for updating user's details. Use public user id in an URI path. " +
                    "For example: users/1122334455")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == principal.publicUserId")
    @PutMapping(path = "/{userId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUser(@PathVariable String userId, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto updatedUser = userService.updateUser(userId, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);
        return returnValue;
    }

    @ApiOperation(value = "The Delete User Web Service Endpoint",
            notes = "This Web Service Endpoint uses for deleting user. Use public user id in an URI path. " +
                    "For example: users/1122334455")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == principal.publicUserId")
    @DeleteMapping(path = "/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String userId) {
        OperationStatusModel operationStatusModel = new OperationStatusModel();
        operationStatusModel.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(userId);
        operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return operationStatusModel;
    }
}
