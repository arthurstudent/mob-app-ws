package com.appsdeveloperblog.app.ws.ui.model.request;

import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequestModel {

    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;

    private UserDto userDetails;

}
