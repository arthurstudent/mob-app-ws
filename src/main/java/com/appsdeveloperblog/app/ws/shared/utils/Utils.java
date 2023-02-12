package com.appsdeveloperblog.app.ws.shared.utils;

import com.appsdeveloperblog.app.ws.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Component
public class Utils {
    private final Random secureRandom = new SecureRandom();
    private static final int BOUND = 10;

    public static boolean hasTokenExpired(String token) {

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token).getBody();

            Date tokenExpirationDate = claims.getExpiration();
            Date todayDate = new Date();
            return tokenExpirationDate.before(todayDate);

        } catch (ExpiredJwtException jwtException) {
            return true;
        }
    }

    public String generateId() {
        return generateRandomString();
    }

    private String generateRandomString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < BOUND; i++) {
            stringBuilder.append(secureRandom.nextInt(BOUND));
        }
        return stringBuilder.toString();
    }

    // TODO simplify in one method
    public String generateEmailVerificationToken(String generateId) {
        return Jwts.builder()
                .setSubject(generateId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EMAIL_VERIFICATION_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
    }

    public String generatePasswordResetToken(String userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
    }
}
