package ru.itmo.michawest.learning.commands;

public class Reorder extends Command{
    protected String nameOfCommand = "reorder";

    public Reorder() {
        super("reorder");
    }

    @Override
    public void getResult() {
        System.out.println("Успешно сменнен порядок сортировки");
    }
}
