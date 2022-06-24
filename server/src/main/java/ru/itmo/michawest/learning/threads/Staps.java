package ru.itmo.michawest.learning.threads;

import ru.itmo.michawest.learning.collection.PersonCollection;
import ru.itmo.michawest.learning.commands.Command;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;

public class Staps {
    private InetSocketAddress addressOfSender;
    private Command command;
    private DatagramChannel channel;
    private PersonCollection collection;
    private int port;
    private Command result;
    private boolean running;
    private Connection connection;
    private boolean ps;

    public Staps(DatagramChannel channel, PersonCollection col, Connection connection) throws IOException {
        this.channel = channel;
        this.collection = col;
        this.running = true;
        this.connection = connection;
        this.ps = false;
    }

    public void setCommand(Command command){
        this.command = command;
    }

    public void setAddressOfSender(InetSocketAddress addressOfSender){
        this.addressOfSender = addressOfSender;
    }

    public Command getResult(){
        return result;
    }

    public InetSocketAddress getAddressOfSender(){
        return addressOfSender;
    }

    public Command getCommand(){
        return command;
    }

    public DatagramChannel getChannel(){
        return channel;
    }

    public void setResult(Command res){
        this.result = res;
    }

    public PersonCollection getCollection(){
        return collection;
    }

    public void setRunning(boolean t){
        this.running = t;
    }

    public boolean getRunning(){
        return running;
    }

    public Connection getConnection(){
        return connection;
    }

    public void setPs(boolean s){
        this.ps = s;
    }

    public boolean getPs(){
        return this.ps;
    }
}
