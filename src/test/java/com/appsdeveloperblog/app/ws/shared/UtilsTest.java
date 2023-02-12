package com.appsdeveloperblog.app.ws.shared;

import com.appsdeveloperblog.app.ws.shared.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {
    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() {

    }

    @Test
    final void testGenerateId() {
        String generateId = utils.generateId();

        assertNotNull(generateId);
        assertEquals(10, generateId.length());
        assertNotEquals(generateId, utils.generateId());
    }

    @Test
    final void testHasTokenNotExpired() {
        String generateEmailVerificationToken = utils.generateEmailVerificationToken("0123456789");

        assertNotNull(generateEmailVerificationToken);

        boolean hasTokenExpired = Utils.hasTokenExpired(generateEmailVerificationToken);

        assertFalse(hasTokenExpired);
    }
    @Test
    @Disabled("wait a few days as for now token is not expired")
    final void testHasTokenExpired() {
        String generateEmailVerificationToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4NTY4NTY4MCIsImV4cCI6MTY3NjgyMjE3M30.luO5jOLLpSRKcxXbS0QFBmWRBFPajb5PlxRlfFGAaPaDfFqaltO0LgfmpRveuPFX9xE_WUCz73QAWGLR6kmRSg";

        boolean hasTokenExpired = Utils.hasTokenExpired(generateEmailVerificationToken);

        assertTrue(hasTokenExpired);
    }


}
