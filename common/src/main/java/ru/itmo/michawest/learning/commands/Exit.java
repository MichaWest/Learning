package ru.itmo.michawest.learning.commands;

public class Exit extends Command{
    protected String nameOfCommand = "exit";

    public Exit() {
        super("exit");
    }

    @Override
    public void getResult() {
        System.out.println("Good buy");
    }

}
