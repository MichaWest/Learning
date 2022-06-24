package ru.itmo.michawest.learning.exception;

import java.sql.SQLException;

public class ExecuteCommandException extends SQLException {
    public ExecuteCommandException(){
        super("Команда не выполнилась");
    }
}
