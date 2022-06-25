package ru.itmo.michawest.learning.commands;

import ru.itmo.michawest.learning.collection.PersonCollection;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Command implements Serializable {
    protected PersonCollection collection;
    protected ArrayList<String> history;
    protected String nameOfCommand;
    protected String user;

    public Command(String name) {
        nameOfCommand = name;
        history = new ArrayList<>();
    }

    abstract public void getResult();

    public void setCollection(PersonCollection collection){
        this.collection = collection;
    }

    public PersonCollection getCollection(){
        return collection;
    }

    public String getNameOfCommand(){
        return nameOfCommand;
    }

    public String getLogin(){
        return user;
    }

    public void setLogin(String login){
        this.user = login;
    }


}
