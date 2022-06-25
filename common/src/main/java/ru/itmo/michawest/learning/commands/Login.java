package ru.itmo.michawest.learning.commands;

import ru.itmo.michawest.learning.commands.Command;

import java.io.Serializable;

public class Login extends Command {
    protected String nameOfCommand = "login";
    private String password;
    private boolean registration;

    public Login(){
        super("login");
        registration = false;
    }

    public Login(String nameOfCommand){
        super("registration");
    }

    public void registration(){
        this.nameOfCommand = "registration";
    }

    public void getResult(){

    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }


}
