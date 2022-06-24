package ru.itmo.michawest.learning.commands;

import ru.itmo.michawest.learning.data.Person;

public class MinByWeight extends Command{
    protected String nameOfCommand = "min_by_weight";
    private Person minPerson;

    public MinByWeight() {
        super("min_by_weight");
    }

    @Override
    public void getResult() {
        System.out.println("The person with min weight: " + minPerson);
    }

    public void setMinPerson(Person p) {
        minPerson = p;
    }

}
