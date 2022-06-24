package ru.itmo.michawest.learning.commands;

public class Mistake extends Command{
    protected String nameOfCommand = "mistake";
    private String message = "На сервере возникла ошибка";

    public Mistake() {
        super("exception");
    }

    public Mistake(String msg) {
        super("exception");
        this.message = msg;
    }


    @Override
    public void getResult() {
        System.out.println(message);
    }

    public void setMessage(String mes) {
        message = mes;
    }
}
