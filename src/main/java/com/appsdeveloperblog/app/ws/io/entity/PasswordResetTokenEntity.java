package com.appsdeveloperblog.app.ws.io.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "password_reset_tokens")
@Getter
@Setter
public class PasswordResetTokenEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String token;
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userDetails;

}
