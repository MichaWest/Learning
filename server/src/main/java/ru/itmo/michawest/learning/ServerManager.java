package ru.itmo.michawest.learning;

import ru.itmo.michawest.learning.collection.PersonCollection;
import ru.itmo.michawest.learning.commands.*;
import ru.itmo.michawest.learning.exception.*;
import ru.itmo.michawest.learning.threads.Execute;
import ru.itmo.michawest.learning.threads.Get;
import ru.itmo.michawest.learning.threads.Send;
import ru.itmo.michawest.learning.threads.Staps;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerManager {
    private PersonCollection collection;
    private DatagramChannel channel;
    private Connection connection;
    private boolean isRunning;
    private int port;
    private ExecutorService receiver;
    private ExecutorService handler;
    private ExecutorService sender;
    private InetSocketAddress addressOfSender;
    private Scanner console = new Scanner(System.in);
    final int MAX_CLIENTS=10;
    private final List<String> history;

    public ServerManager(PersonCollection col, int port){
        collection = col;
        this.port = port;
        receiver = Executors.newFixedThreadPool(MAX_CLIENTS);
        handler = Executors.newCachedThreadPool();
        sender = Executors.newFixedThreadPool(MAX_CLIENTS);
        history = new ArrayList<>();
    }
    public void work() throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        isRunning = true;

        channel = DatagramChannel.open();
        addressOfSender = new InetSocketAddress("localhost",port);
        channel.bind(addressOfSender);

        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/personcollection", "postgres", "micha2003");

        new Thread(() -> {
            checkConsole();
        }).start();
        System.out.println("Хочу начать работу");
        while (isRunning) {
            receiver.submit(() -> {
                try {
                    Command current = getCommand();
                    System.out.println("Команда от "+current.getLogin()+" получена");
                    handler.submit(() -> {
                        executeCommand(current);
                        System.out.println("Команда от "+current.getLogin()+" выполнена");
                        sender.submit(() -> {

                            try {
                                sendCommand(current);
                                System.out.println("Команда отправлена пользователю: "+current.getLogin());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        });
                    });
                } catch (ClosedByInterruptException e) {
                    System.out.println("Сервер завершил работу");
                } catch (IOException e) {

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    void close(){
        isRunning=false;
        receiver.shutdownNow();
        //shutdownNow
        handler.shutdown();
        sender.shutdown();
    }

    private void checkConsole(){
        while(isRunning) {
            String str = console.nextLine();
            if (str.toLowerCase().trim().equals("exit")) {
                close();
            } else {
                System.out.println("Чтобы выключить сервер введите exit");
            }
        }
    }

    private Command getCommand() throws IOException, ClassNotFoundException {
        ByteBuffer buf = ByteBuffer.allocate(1024 * 1024);
        addressOfSender = (InetSocketAddress) channel.receive(buf);
        ByteArrayInputStream readbuf = new ByteArrayInputStream(buf.array());
        ObjectInputStream readOb = new ObjectInputStream(readbuf);
        return (Command) readOb.readObject();
    }

    private void sendCommand(Command current) throws IOException {
        ByteArrayOutputStream writebuf = new ByteArrayOutputStream(1024*1024);
        ObjectOutputStream writeOb = new ObjectOutputStream(writebuf);
        writeOb.writeObject(current);
    }

    private Command executeCommand(Command current){
        Statement stmt = null;
        String com = current.getNameOfCommand();
        history.add(com);
        try {
            switch (com) {
                case("login"):
                    Login login = (Login) current;
                    return login;
                case("registration"):
                    Login registration = (Login) current;
                    stmt = connection.createStatement();
                    stmt.executeQuery("INSERT INTO users (login, password) VALUES (\'"+registration.getLogin()+"\', \'"+getCode(registration.getPassword())+"\');");
                    System.out.println("INSERT INTO users (login, password) VALUES (\'"+registration.getLogin()+"\', \'"+getCode(registration.getPassword())+"\');");
                    return registration;
                case ("info"):
                    Info info = (Info) current;
                    info.setCollection(collection);
                    return info;
                case ("show"):
                    Show show = (Show) current;
                    if (collection.getCollection() == null) throw new EmptyCollectionException();
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    show.setCollection(collection);
                    show.getResult();
                    return show;
                case ("add"):
                    Add add = (Add) current;
                    collection.add(add.getPerson());
                    //добавить в базу данных
                    add.setCollection(collection);
                    return (add);
                case ("update_by_id"):
                    UpdateById updateById = (UpdateById) current;
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    if (!collection.checkId(updateById.getNewId()))
                        throw new InvalidCommandArgumentException("нет такого id");
                    collection.updateById(updateById.getNewId(), updateById.getUpdatePerson());
                    updateById.setCollection(collection);
                    return (updateById);
                case ("remove_by_id"):
                    RemoveById removeById = (RemoveById) current;
                    int id = removeById.getRemoveID();
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    if (!collection.checkId(id)) throw new InvalidCommandArgumentException("нет такого id");
                    collection.removeById(id);
                    removeById.setCollection(collection);
                    return(removeById);
                case ("clear"):
                    Clear clear = (Clear) current;
                    collection.clear();
                    clear.setCollection(collection);
                    return(clear);
                case ("execute_script"):
                    ExecuteScript executeScript = (ExecuteScript) current;
                    executeScript.setCollection(collection);
                    return(executeScript);
                case ("remove_first"):
                    RemoveFirst removeFirst = new RemoveFirst();
                    if(!collection.checkId(0)) throw new InvalidCommandArgumentException("Нет элемента с таким id");
                    collection.remove(0);
                    removeFirst.setCollection(collection);
                    return(removeFirst);
                case ("reorder"):
                    Reorder reorder = (Reorder) current;
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    collection.reorder();
                    reorder.setCollection(collection);
                    return(reorder);
                case ("history"):
                    History history = (History) current;
                    history.setCollection(collection);
                    return(history);
                case ("min_by_weight"):
                    MinByWeight minByWeight = (MinByWeight) current;
                    minByWeight.setMinPerson(collection.minByWeight());
                    minByWeight.setCollection(collection);
                    return(minByWeight);
                case ("group_counting_by_nationality"):
                    GroupCountingByNationality groupCountingByNationality = (GroupCountingByNationality) current;
                    groupCountingByNationality.setGroup(collection.groupByNationality(groupCountingByNationality.getNationality()));
                    groupCountingByNationality.setCollection(collection);
                    return(groupCountingByNationality);
                case ("count_by_hair_color"):
                    CountByHairColor countByHairColor = (CountByHairColor) current;
                    countByHairColor.setCount(collection.countByHairColor(countByHairColor.getColor()));
                    countByHairColor.setCollection(collection);
                    return(countByHairColor);
                case ("help"):
                    Help help = (Help) current;
                    help.setCollection(collection);
                    return (help);
            }
        } catch (InvalidCommandArgumentException e) {
            System.out.println(e.getMessage());
            Mistake exception = new Mistake();
            exception.setMessage(e.getMessage());
            exception.setCollection(collection);
            return (exception);
        } catch (EmptyCollectionException | CannotSaveException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
            Mistake exception = new Mistake();
            exception.setMessage(e.getMessage());
            exception.setCollection(collection);
            return (exception);
        } catch (CommandException e) {
            System.out.println(e.getMessage());
            Mistake exception = new Mistake();
            exception.setMessage(e.getMessage());
            exception.setCollection(collection);
            System.out.println(com);
            return (exception);
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            Mistake exception = new Mistake();
            exception.setMessage("Не удалось расшифровать пароль");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Mistake mistake = new Mistake();
        mistake.setMessage("Вы отправили не сущетсвующую команду");
        return mistake;
    }

    private String getCode(String psw) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bytes = md.digest(psw.getBytes());
        String ret = "";
        for(byte b: bytes){
            ret = ret+b+" ";
        }
        return ret;
    }

}
