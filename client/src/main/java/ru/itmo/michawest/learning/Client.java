package ru.itmo.michawest.learning;

import ru.itmo.michawest.learning.Input.ConsoleInput;

import java.io.IOException;
import java.nio.channels.DatagramChannel;

public class Client {
    public static void main(String[] args){
        int serverPort = 5790;
        System.out.println("Добро пожаловать!");
        try{
            serverPort = Integer.parseInt(args[0]);
        }catch (NumberFormatException e){
            System.out.println("Вы вели неправильный формат для номера порта. Подключение произойдет к порту по умолчанию: "+serverPort);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Вы ничего не вели. Подключение произойдет к порту по умолчанию: "+serverPort);
        }
        try {
            ConsoleInput input = new ConsoleInput();
            ClientManager client = new ClientManager(serverPort, input);
            client.execute();
        }catch(IOException e){
            System.out.println("Возникла ошибка при подключении");
        }
    }
}
