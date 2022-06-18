package com.abn.recipeman.adapter.model.errors;


import java.util.HashMap;
import java.util.Map;

public class BadRequestAlertException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    private final String errorKey;


    public BadRequestAlertException( String defaultMessage, String entityName, String errorKey) {
        super(defaultMessage);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "error." + errorKey);
        parameters.put("params", entityName);
        return parameters;
    }
}
