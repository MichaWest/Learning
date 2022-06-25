package ru.itmo.michawest.learning.exception;

import java.io.IOException;

public class AccessException extends CommandException {

    public AccessException(){
        super("У вас недостаточно прав");
    }
}
