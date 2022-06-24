package ru.itmo.michawest.learning;

import ru.itmo.michawest.learning.collection.PersonCollection;
import ru.itmo.michawest.learning.threads.Staps;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            PersonCollection personCollection = new PersonCollection();
            ServerManager manager = new ServerManager(personCollection, port);
            manager.work();
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("При запуске введите порт");
        }catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("Не задан порт. Завершение работы программы.");
        }catch (IllegalArgumentException e){
            System.out.println("Некорректное значение порта. Завершение работы приложения.");
        } catch (IOException e) {
            System.out.println("Порт уже занят. Попробуйте вести другой");
        } catch (InterruptedException e) {
            System.out.println("Сервер был прерван.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Не удалось подключится к postgreSQL");
        }
    }
}
