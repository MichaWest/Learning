package ru.itmo.michawest.learning.exception;

import java.io.IOException;

public class AbsenceAnswerException extends IOException {
    public AbsenceAnswerException(){
        super("Нет ответа от сервера. Попробуйте позже.");
    }
}
