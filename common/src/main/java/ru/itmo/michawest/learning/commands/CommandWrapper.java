package ru.itmo.michawest.learning.commands;

public class CommandWrapper {
    private String arg;
    private String com;

    public CommandWrapper(String str){
        String[] argAndCom = str.split(" ");
        if(argAndCom.length==1){
            com = argAndCom[0];
            arg = "";
        }
        if(argAndCom.length>1){
            com = argAndCom[0];
            arg = argAndCom[1];
        }

    }

    public CommandWrapper(String com, String arg){
        this.com = com;
        this.arg = arg;
    }

    public String getCom(){
        return com;
    }

    public String getArg(){
        return arg;
    }
}
