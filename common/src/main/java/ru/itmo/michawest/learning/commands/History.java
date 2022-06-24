package ru.itmo.michawest.learning.commands;

public class History extends Command{
    protected String nameOfCommand = "history";

    public History() {
        super("history");
    }

    @Override
    public void getResult() {
        for (String command : this.history) {
            System.out.println(command);
        }
    }
}
