package com.appsdeveloperblog.app.ws.io.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;

    private String emailVerificationToken;

    @Column(columnDefinition = "boolean default false")
    private Boolean emailVerificationStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<AddressEntity> addressEntityList = new ArrayList<>();

    public void setAddress(List<AddressEntity> addressEntityList) {
        this.addressEntityList = addressEntityList;
        addressEntityList.forEach(address -> address.setUser(this));
    }
    public UserEntity() {
    }
}
