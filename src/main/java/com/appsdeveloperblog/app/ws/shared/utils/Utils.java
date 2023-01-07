package com.appsdeveloperblog.app.ws.shared.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class Utils {
    private final Random RANDOM = new SecureRandom();
    private int Return;


    public int generateUserId(int length) {
        return generateRandomString(length);
    }
    public int generateAddressId(int length) {
        return generateRandomString(length);
    }

    private int generateRandomString(int length) {

        for (int i = 0; i < length; i++) {
            Return+=RANDOM.nextInt(1000000000);
        }
        return Return;
    }
}
