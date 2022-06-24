package ru.itmo.michawest.learning.commands;

import ru.itmo.michawest.learning.data.Person;

public class UpdateById extends Command{
    protected String nameOfCommand = "update_by_id";
    private int newId;
    private Person per;

    public UpdateById() {
        super("update_by_id");
    }

    @Override
    public void getResult() {
        System.out.println("Эдемент #" + newId + " обновлен");
    }

    public void setNewId(int id) {
        newId = id;
    }

    public int getNewId() {
        return newId;
    }

    public void setUpdatePerson(Person p) {
        per = p;
    }

    public Person getUpdatePerson() {
        return per;
    }
}
