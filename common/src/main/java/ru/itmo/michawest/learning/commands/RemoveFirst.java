package ru.itmo.michawest.learning.commands;

public class RemoveFirst extends Command{
    protected String nameOfCommand = "remove_first";

    public RemoveFirst() {
        super("remove_first");
    }

    @Override
    public void getResult() {
        System.out.println("Первый элемент успешно удален");
    }
}
