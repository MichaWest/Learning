package ru.itmo.michawest.learning.exception;

public class CannotSaveException extends RuntimeException{
    public CannotSaveException(String str){
        super(str);
    }
}
