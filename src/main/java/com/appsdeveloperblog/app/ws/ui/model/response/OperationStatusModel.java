package com.appsdeveloperblog.app.ws.ui.model.response;

public class OperationStatusModel {
    private String operationResult;
    private String operationName;

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public String getOperationName() {
        return operationName;
    }
}
