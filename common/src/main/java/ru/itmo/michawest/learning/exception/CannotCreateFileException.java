package ru.itmo.michawest.learning.exception;

public class CannotCreateFileException extends FileException {
    public CannotCreateFileException() {
        super("немогу создать файл");
    }
}
