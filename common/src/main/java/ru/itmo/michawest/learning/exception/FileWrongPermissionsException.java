package ru.itmo.michawest.learning.exception;

import java.io.IOException;

public class FileWrongPermissionsException extends IOException {
    public FileWrongPermissionsException(String s) {
        super(s);
    }
}
