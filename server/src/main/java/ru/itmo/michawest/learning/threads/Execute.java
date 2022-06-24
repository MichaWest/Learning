package ru.itmo.michawest.learning.threads;

import ru.itmo.michawest.learning.SendService;
import ru.itmo.michawest.learning.collection.PersonCollection;
import ru.itmo.michawest.learning.commands.*;
import ru.itmo.michawest.learning.exception.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Execute implements Runnable{
    Staps staps;
    private Send send;
    private SendService sendService;
    private Semaphore sem;
    private final List<String> history;
    private Connection connection;

    public Execute(Staps staps) throws IOException {
        this.staps = staps;
        this.send = new Send(staps);
        this.sendService = new SendService(send);
        sem = new Semaphore(1);
        history = new ArrayList<>();
    }

    @Override
    public void run(){
        try {
            sem.acquire();
            connection = staps.getConnection();
            staps.setResult(runCommand(staps.getCommand()));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("Команда выполнена");
        try {
            Statement stmt = connection.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM users;");
            while(res.next()){
                System.out.println(res.getString("login"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sem.release();
        sendService.run();
    }

    public void closePool(){
        sendService.close();
    }

    private Command runCommand(Command command) throws IOException {
        String com = command.getNameOfCommand();
        PersonCollection collection = staps.getCollection();
        history.add(com);

        Statement stmt = null;
        //проверка о принадлежности ползьвателю объекта
        try {
            switch (com) {
                case("login"):
                    Login login = (Login) command;
                    return login;
                case("registration"):
                    Login registration = (Login) command;
                    stmt = connection.createStatement();
                    stmt.executeQuery("INSERT INTO users (login, password) VALUES (\'"+registration.getLogin()+"\', \'"+getCode(registration.getPassword())+"\');");
                    System.out.println("INSERT INTO users (login, password) VALUES (\'"+registration.getLogin()+"\', \'"+12345678+"\');");
                    return registration;
                case ("info"):
                    Info info = (Info) command;
                    info.setCollection(collection);
                    return info;
                case ("show"):
                    Show show = (Show) command;
                    if (collection.getCollection() == null) throw new EmptyCollectionException();
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    show.setCollection(collection);
                    show.getResult();
                    return show;
                case ("add"):
                    Add add = (Add) command;
                    collection.add(add.getPerson());
                    //добавить в базу данных
                    add.setCollection(collection);
                    return (add);
                case ("update_by_id"):
                    UpdateById updateById = (UpdateById) command;
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    if (!collection.checkId(updateById.getNewId()))
                        throw new InvalidCommandArgumentException("нет такого id");
                    collection.updateById(updateById.getNewId(), updateById.getUpdatePerson());
                    updateById.setCollection(collection);
                    return (updateById);
                case ("remove_by_id"):
                    RemoveById removeById = (RemoveById) command;
                    int id = removeById.getRemoveID();
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    if (!collection.checkId(id)) throw new InvalidCommandArgumentException("нет такого id");
                    collection.removeById(id);
                    removeById.setCollection(collection);
                    return(removeById);
                case ("clear"):
                    Clear clear = (Clear) command;
                    collection.clear();
                    clear.setCollection(collection);
                    return(clear);
                case ("save"):
                    Save save = (Save) command;

                    save.setCollection(collection);
                    System.out.println("Send");
                    return(save);
                case ("execute_script"):
                    ExecuteScript executeScript = (ExecuteScript) command;
                    executeScript.setCollection(collection);
                    return(executeScript);
                case ("exit"):
                    Exit exit = (Exit) command;
                    //sendResult(exit);
                    return exit;
                case ("remove_first"):
                    RemoveFirst removeFirst = new RemoveFirst();
                    if(!collection.checkId(0)) throw new InvalidCommandArgumentException("Нет элемента с таким id");
                    collection.remove(0);
                    removeFirst.setCollection(collection);
                    return(removeFirst);
                case ("reorder"):
                    Reorder reorder = new Reorder();
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    collection.reorder();
                    reorder.setCollection(collection);
                    return(reorder);
                case ("history"):
                    History history = new History();
                    history.setCollection(collection);
                    return(history);
                case ("min_by_weight"):
                    MinByWeight minByWeight = new MinByWeight();
                    minByWeight.setMinPerson(collection.minByWeight());
                    minByWeight.setCollection(collection);
                    return(minByWeight);
                case ("group_counting_by_nationality"):
                    GroupCountingByNationality groupCountingByNationality = (GroupCountingByNationality) command;
                    groupCountingByNationality.setGroup(collection.groupByNationality(groupCountingByNationality.getNationality()));
                    groupCountingByNationality.setCollection(collection);
                    return(groupCountingByNationality);
                case ("count_by_hair_color"):
                    CountByHairColor countByHairColor = (CountByHairColor) command;
                    countByHairColor.setCount(collection.countByHairColor(countByHairColor.getColor()));
                    countByHairColor.setCollection(collection);
                    return(countByHairColor);
                case ("help"):
                    Help help = (Help) command;
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
