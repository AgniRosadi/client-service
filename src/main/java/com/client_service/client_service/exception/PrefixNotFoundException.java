package com.client_service.client_service.exception;

import java.io.Serial;

public class PrefixNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -8557156813605818209L;

    public PrefixNotFoundException(String message) {
        super(message);
    }
}
