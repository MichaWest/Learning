package ru.itmo.michawest.learning.exception;

public class MissedCommandArgumentException extends CommandException {
    public MissedCommandArgumentException() {
        super("пропущен аргумент для команды");
    }
}
