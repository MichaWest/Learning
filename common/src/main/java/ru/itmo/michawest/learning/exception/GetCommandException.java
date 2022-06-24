package ru.itmo.michawest.learning.exception;

import java.io.IOException;

public class GetCommandException extends IOException {
    public GetCommandException(){
        super("Возникла ошибка при получении результата");
    }
}
