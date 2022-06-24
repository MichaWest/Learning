package ru.itmo.michawest.learning.commands;

import ru.itmo.michawest.learning.collection.PersonCollection;

public class Clear extends Command{
    protected String nameOfCommand = "clear";

    public Clear() {
        super("clear");
    }

    @Override
    public void getResult() {
        System.out.println("Коллекция очищена");
    }

    public void setCollection(PersonCollection col) {
        this.collection = col;
    }
}
