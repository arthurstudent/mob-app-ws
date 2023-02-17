package com.appsdeveloperblog.app.ws.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestModel {
    private String email;
    private String password;
}
