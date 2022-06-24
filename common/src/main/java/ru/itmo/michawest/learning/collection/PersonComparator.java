package ru.itmo.michawest.learning.collection;

import ru.itmo.michawest.learning.data.Person;

import java.util.Comparator;

public class PersonComparator implements Comparator<Person> {
    private boolean order;

    public PersonComparator(boolean ord){
        this.order = ord;
    }

    public int compare(Person a, Person b){
        if(order){
            return (int)(a.getHeight() - b.getHeight());
        }else{
            return (int)(a.getHeight() - a.getHeight());
        }
    }
}
