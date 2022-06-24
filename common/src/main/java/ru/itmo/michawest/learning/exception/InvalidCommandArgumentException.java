package ru.itmo.michawest.learning.exception;

public class InvalidCommandArgumentException extends CommandException {
    public InvalidCommandArgumentException(String s) {
        super(s);
    }
}
