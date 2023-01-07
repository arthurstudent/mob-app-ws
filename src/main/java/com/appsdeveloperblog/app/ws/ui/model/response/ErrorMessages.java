package com.appsdeveloperblog.app.ws.ui.model.response;

public enum ErrorMessages {

    MISSING_REQUIRED_FIELD("Missing a required field"),
    RECORD_ALREADY_EXISTS("Record already exists"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    NO_RECORD_FOUND("Record with provided id is not found"),
    AUTHENTICATION_FAILED("Authentication failed"),
    COULD_NOT_UPDATE_THE_RECORD("Could not update the record"),
    COULD_NOT_DELETE_THE_RECORD("Could not delete the record"),
    EMAIL_ADDRESS_NOT_VERIFIED("Email could not be verified");

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }
}
