package ru.itmo.michawest.learning.exception;

import java.io.IOException;

public class EmptyPathException extends IOException {
    public EmptyPathException(){
        super("Путь пустой");
    }
}
