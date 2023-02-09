package com.appsdeveloperblog.app.ws.shared.utils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

public class AmazonSES {

    // This address must be verified with Amazon SES.
    static final String FROM = "vandorn91011@gmail.com";

    // The subject line for the email.
    static final String SUBJECT = "One last step to complete your registration with PhotoApp";

    static final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // The HTML body for the email.
    static final String HTML_BODY = "<h1>Please verify your email address</h1>"
            + "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " click on the following link: "
            + "<a href='http://localhost:8080/email-verification-service/email-verification.html?token=$tokenValue'>"
            + "Final step to complete your registration" + "</a><br/><br/>"
            + "Thank you! And we are waiting for you inside!";

    // The email body for recipients with non-HTML email clients.
    static final String TEXT_BODY = "Please verify your email address. "
            + "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " http://localhost:8080/email-verification-service/email-verification-service/email-verification.html?token=$tokenValue"
            + " Thank you! And we are waiting for you inside!";


    static final String PASSWORD_RESET_HTML_BODY = "<h1>A request to reset your password</h1>"
            + "<p>Hi, $firstName!</p> "
            + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please click on the link below to set a new password: "
            + "<a href='http://localhost:8080/email-verification-service/password-reset.html?token=$tokenValue'>"
            + " Click this link to Reset Password"
            + "</a><br/><br/>"
            + "Thank you!";

    // The email body for recipients with non-HTML email clients.
    static final String PASSWORD_RESET_TEXT_BODY = "A request to reset your password "
            + "Hi, $firstName! "
            + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"
            + " http://localhost:8080/email-verification-service/password-reset.html?token=$tokenValue"
            + " Thank you!";


    public void verifyEmail(UserDto userDto) {

        // You can also set your keys this way. And it will work!
        // System.setProperty("aws.accessKeyId", "<YOUR KEY ID HERE>");
        // System.setProperty("aws.secretKey", "<SECRET KEY HERE>");
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_NORTH_1)
                .build();

        String htmlBodyWithToken = HTML_BODY.replace("$tokenValue", userDto.getEmailVerificationToken());
        String textBodyWithToken = TEXT_BODY.replace("$tokenValue", userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);
    }

    public boolean sendPasswordResetRequest(String firstName, String email, String token)
    {
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.EU_NORTH_1).build();

        String htmlBodyWithToken = PASSWORD_RESET_HTML_BODY.replace("$tokenValue", token);
        htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);

        String textBodyWithToken = PASSWORD_RESET_TEXT_BODY.replace("$tokenValue", token);
        textBodyWithToken = textBodyWithToken.replace("$firstName", firstName);


        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
                .withSource(FROM);
        // Exception handle
        SendEmailResult result = client.sendEmail(request);

        return result != null && (result.getMessageId() != null && !result.getMessageId().isEmpty());
    }

    public String replace(String result, String target, String replacement){
        return result.replace(target, replacement);
    }
}
