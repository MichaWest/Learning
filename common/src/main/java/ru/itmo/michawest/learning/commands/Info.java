package ru.itmo.michawest.learning.commands;

import ru.itmo.michawest.learning.exception.EmptyCollectionException;

public class Info extends Command{
    protected String nameOfCommand = "info";

    public Info() {
        super("info");
    }

    @Override
    public void getResult() {
        if (collection.getCollection() == null) throw new EmptyCollectionException();
        if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
        System.out.println(collection.getInfo());
    }
}
