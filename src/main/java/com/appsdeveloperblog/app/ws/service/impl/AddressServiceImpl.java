package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.AddressRepository;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    UserRepository userRepository;
    AddressRepository addressRepository;

    public AddressServiceImpl(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressDTO> getAddresses(String userId) {
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            return new ArrayList<>();
        }
        List<AddressEntity> addressEntityList = addressRepository.findAllByUser(userEntity);

        return addressEntityList
                .stream()
                .map(addressEntity -> modelMapper.map(addressEntity, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressEntity addressEntity = addressRepository.findAllByAddressId(addressId);
        return addressEntity != null ? new ModelMapper().map(addressEntity, AddressDTO.class) : null;
    }
}
