package com.appsdeveloperblog.app.ws.shared.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class Utils {
    private final Random RANDOM = new SecureRandom();
    private int Return;

    private static final int BOUND = 9;


    public long generateId(int length) {
        return generateRandomString(length);
    }
    private long generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(RANDOM.nextInt(BOUND + 1));
        }
        return Integer.parseInt(stringBuilder.toString());
    }
}
