package ru.itmo.michawest.learning.commands;

import ru.itmo.michawest.learning.data.Person;

public class Add extends Command{
    protected String nameOfCommand = "add";
    private Person person;

    public Add() {
        super("add");
    }

    @Override
    public void getResult() {
        System.out.println("Person " + person.getName() + " добавлен в коллекцию");
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person p) {
        this.person = p;
    }
}
