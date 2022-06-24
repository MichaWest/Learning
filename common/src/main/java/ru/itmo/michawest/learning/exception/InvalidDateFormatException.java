package ru.itmo.michawest.learning.exception;

public class InvalidDateFormatException extends ParameterException{
    public InvalidDateFormatException(){
        super("Неправильный формаь даты");
    }
}
