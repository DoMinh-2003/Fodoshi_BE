package com.BE.exception.exceptions;

public class DuplicateException extends RuntimeException{
    public DuplicateException(String message){
        super(message);
    }
}
