package ru.itmo.michawest.learning.exception;

public class InvalidEnumException extends ParameterException {
    public InvalidEnumException() {
        super("неправильная константа");
    }
}
