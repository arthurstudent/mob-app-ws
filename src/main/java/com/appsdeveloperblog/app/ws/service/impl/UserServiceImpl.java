package com.appsdeveloperblog.app.ws.service.impl;


import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.shared.utils.Utils;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    Utils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepository.findUserByEmail(user.getEmail()) != null) throw new RuntimeException("Already exist");

//        modelMapper1.getConfiguration().setAmbiguityIgnored(true);
//        PropertyMap<AddressDTO, AddressEntity> clientPropertyMap = new PropertyMap<>() {
//            @Override
//            protected void configure() {
//                skip(source.getUserDetails());
//            }
//        };
//        modelMapper1.addMappings(clientPropertyMap);
//        modelMapper.getConfiguration()
//                .setFieldMatchingEnabled(true)
//                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
//        List<AddressEntity> addressEntityList = new ArrayList<>();

        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDTO addressDTO = user.getAddresses().get(i);
//            addressDTO.setUserDetails(user);
            addressDTO.setAddressId(String.valueOf(utils.generateId(8)));
            user.getAddresses().set(i, addressDTO);
        }


        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(String.valueOf(utils.generateId(8)));


//        ModelMapper modelMapper1 = new ModelMapper();
//        modelMapper1.getConfiguration().setAmbiguityIgnored(true);
//        PropertyMap<AddressDTO, AddressEntity> clientPropertyMap = new PropertyMap<>() {
//            @Override
//            protected void configure() {
//                skip(source.getUserDetails());
//            }
//        };
//        modelMapper1.addMappings(clientPropertyMap);
//        TypeMap<AddressDTO, AddressEntity> propertyMapper = modelMapper1.createTypeMap(AddressDTO.class, AddressEntity.class);
//        propertyMapper.addMappings(mapper -> mapper.skip(AddressEntity::setUserDetails));
//        List<AddressEntity> addressEntityList = user.getAddresses()
//                .stream()
//                .map(addressDTO -> modelMapper.map(addressDTO, AddressEntity.class))
//                .collect(Collectors.toList());


        List<AddressEntity> addressEntityList
                = modelMapper.map(user.getAddresses(),
                new TypeToken<List<AddressEntity>>() {}.getType());

        userEntity.setAddress(addressEntityList);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnedUserDto = modelMapper.map(storedUserDetails, UserDto.class);
        returnedUserDto.setAddresses(modelMapper
                .map(storedUserDetails.getAddressEntityList(),
                new TypeToken<List<AddressDTO>>() {}.getType()));
        return returnedUserDto;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userByEmail = userRepository.findUserByEmail(email);
        if (userByEmail == null) throw new UsernameNotFoundException(email);
        UserDto returnedValue = new UserDto();
        BeanUtils.copyProperties(userByEmail, returnedValue);
        return returnedValue;
    }

    @Override
    public UserDto getUserById(String userId) {
        UserDto returnValue = new UserDto();
        UserEntity userByEmail = userRepository.findByUserId(userId);
        if (userByEmail == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        BeanUtils.copyProperties(userByEmail, returnValue);
        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        UserEntity save = userRepository.save(userEntity);
        BeanUtils.copyProperties(save, returnValue);
        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getListUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        if(page>0) page = page - 1;
        Pageable pageable = PageRequest.of(page, limit);
        Page<UserEntity> userPage = userRepository.findAll(pageable);
        List<UserEntity> users = userPage.getContent();
        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userByEmail = userRepository.findUserByEmail(email);
        if (userByEmail == null) throw new UsernameNotFoundException(email);
        return new User(userByEmail.getEmail(), userByEmail.getEncryptedPassword(), new ArrayList<>());
    }
}
