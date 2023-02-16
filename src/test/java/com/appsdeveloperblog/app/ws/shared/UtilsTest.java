package com.appsdeveloperblog.app.ws.shared;

import com.appsdeveloperblog.app.ws.shared.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
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
    final void testHasTokenExpired() {
        String generateEmailVerificationToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1MDEzNzk3MDIyIiwiZXhwIjoxNjc2Mjk1OTE3fQ.cJX9ewSCiuNCZmEjnQhXxhKJAXb7OuT2Guh8D6xpKniSSIM1pTe1DW_xw-8MBwnvs5QVNjzNabwGA925dTC84g";

        boolean hasTokenExpired = Utils.hasTokenExpired(generateEmailVerificationToken);

        assertTrue(hasTokenExpired);
    }
}
