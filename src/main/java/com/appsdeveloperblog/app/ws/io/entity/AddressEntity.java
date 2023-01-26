package com.appsdeveloperblog.app.ws.io.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
@Getter
@Setter
@Entity(name = "addresses")
public class AddressEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 30, nullable = false)
    private String addressId;
    @Column(length = 20, nullable = false)
    private String city;
    @Column(length = 30, nullable = false)
    private String country;
    @Column(length = 100, nullable = false)
    private String streetName;
    @Column(length = 10, nullable = false)
    private String postalCode;
    @Column(length = 15, nullable = false)
    private String type;
    @ManyToOne(cascade = CascadeType.ALL)
    private UserEntity user;

}
