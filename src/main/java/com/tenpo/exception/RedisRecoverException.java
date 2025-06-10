package com.tenpo.exception;

public class RedisRecoverException extends RuntimeException {
    public RedisRecoverException(String message) {
        super(message);
    }
}
