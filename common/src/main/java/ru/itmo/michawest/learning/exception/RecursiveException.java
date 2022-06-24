package ru.itmo.michawest.learning.exception;

public class RecursiveException extends FileException {
    public RecursiveException() {
        super("файл уже запущен");
    }
}
