package ru.itmo.michawest.learning.exception;

import java.io.IOException;

public class SendCommandException extends IOException {

    public SendCommandException(){
        super("Возникла ошибка при отправлении команды на сервер");
    }

}
