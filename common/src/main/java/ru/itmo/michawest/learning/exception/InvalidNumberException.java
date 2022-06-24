package ru.itmo.michawest.learning.exception;

public class InvalidNumberException extends ParameterException {
    public InvalidNumberException(String message) {
        super(message);
    }

    public InvalidNumberException() {
        super("неправильно введено значение");
    }
}
