package ru.itmo.michawest.learning.commands;

public class Show extends Command{
    protected String nameOfCommand = "show";

    public Show() {
        super("show");
    }

    @Override
    public void getResult() {
        System.out.println(collection.serializeCollection());
    }
}
