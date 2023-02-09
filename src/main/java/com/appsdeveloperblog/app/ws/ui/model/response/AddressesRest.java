package com.appsdeveloperblog.app.ws.ui.model.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@Getter
@Setter
public class AddressesRest extends RepresentationModel<AddressesRest> {
    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        if (!super.equals(o)) return false;

        AddressesRest that = (AddressesRest) o;
        return Objects.equals(addressId, that.addressId)
                && Objects.equals(city, that.city)
                && Objects.equals(country, that.country)
                && Objects.equals(streetName, that.streetName)
                && Objects.equals(postalCode, that.postalCode)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), addressId, city, country, streetName, postalCode, type);
    }
}
