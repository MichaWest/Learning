package ru.itmo.michawest.learning.exception;

public class EmptyCollectionException extends CommandException {
    public EmptyCollectionException() {
        super("коллекция пустая");
    }
}

