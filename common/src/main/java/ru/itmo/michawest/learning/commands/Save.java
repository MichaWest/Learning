package ru.itmo.michawest.learning.commands;

public class Save extends Command{
    protected String nameOfCommand = "save";

    public Save() {
        super("save");
    }

    @Override
    public void getResult() {
        System.out.println("Коллекция успешно сохранена");
    }

    public void setNameOfCommand(String str){super.nameOfCommand = str;
    }
}
