package ru.itmo.michawest.learning.commands;

public class ExecuteScript extends Command{
    protected String nameOfCommand = "execute_script";

    public ExecuteScript() {
        super("execute_script");
    }

    @Override
    public void getResult() {
        System.out.println("Файл выполнился");
    }
}
