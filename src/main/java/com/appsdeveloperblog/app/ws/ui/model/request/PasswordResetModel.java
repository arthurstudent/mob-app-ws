package com.appsdeveloperblog.app.ws.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetModel {
    private String token;
    private String password;
}
