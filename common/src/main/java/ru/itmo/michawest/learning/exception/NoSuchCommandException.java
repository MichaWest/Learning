package ru.itmo.michawest.learning.exception;

public class NoSuchCommandException extends CommandException {
    public NoSuchCommandException() {
        super("неправильная команда");
    }
}
