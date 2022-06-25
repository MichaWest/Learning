package ru.itmo.michawest.learning;

import ru.itmo.michawest.learning.collection.DataConverter;
import ru.itmo.michawest.learning.collection.PersonCollection;
import ru.itmo.michawest.learning.commands.*;
import ru.itmo.michawest.learning.data.Person;
import ru.itmo.michawest.learning.exception.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
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
import java.util.concurrent.Semaphore;

public class ServerManager {
    private PersonCollection collection;
    private DatagramChannel channel;
    private Connection connection;
    private boolean isRunning;
    private final int port;
    private final ExecutorService receiver;
    private final ExecutorService handler;
    private final ExecutorService sender;
    private InetSocketAddress addressOfSender;
    private final Scanner console = new Scanner(System.in);
    final int MAX_CLIENTS=3;
    private static List<String> history;
    private static List<String> users;

    public ServerManager( int port) throws ClassNotFoundException, SQLException {
        this.port = port;
        receiver = Executors.newFixedThreadPool(MAX_CLIENTS);
        handler = Executors.newCachedThreadPool();
        sender = Executors.newFixedThreadPool(MAX_CLIENTS);
        history = new ArrayList<>();
        users = new ArrayList<>();
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/personcollection", "postgres", "micha2003");
        try {
            collection = readCollection();
        }catch (SQLException | ParameterException e){
            collection = new PersonCollection();
        }
    }

    public void work() throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        isRunning = true;

        channel = DatagramChannel.open();
        addressOfSender = new InetSocketAddress("localhost",port);
        channel.bind(addressOfSender);

        getUsers();
        System.out.println("Хочу начать работу");
        //new Thread(this::checkConsole).start();
        while (isRunning) {
            receiver.submit(() -> {
                try {
                    //System.out.println("Start getting");
                    //sem.acquire();
                    Command current = getCommand();
                    //sem.release();
                    handler.submit(() -> {
                        System.out.println("Start executing");
                        Command result = executeCommand(current);
                        sender.submit(() -> {
                            System.out.println("Start sending");
                            try {
                                sendCommand(result);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("end sending");
                        });
                        System.out.println("end executing");
                    });
                }catch(SocketException e){
                    checkConsole();
                }catch (ClosedByInterruptException e) {
                    System.out.println("Сервер завершил работу");
                } catch (IOException e) {

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                checkConsole();
            });
        }
    }

    private void  close(){
        isRunning=false;
        receiver.shutdownNow();
        handler.shutdownNow();
        sender.shutdown();
    }

    private void checkConsole(){
        while(isRunning) {
            if(console.hasNext()) {
                String str = console.nextLine();
                if (str.toLowerCase().trim().equals("exit")) {
                    close();
                } else {
                    System.out.println("Чтобы выключить сервер введите exit");
                }
            }
        }
    }

    private Command getCommand() throws ClassNotFoundException, IOException {
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
        ByteBuffer buf =  ByteBuffer.wrap(writebuf.toByteArray());
        channel.send(buf, addressOfSender);
    }

    private Command executeCommand(Command current){
        Statement stmt = null;
        String com = current.getNameOfCommand();
        history.add(com);
        try {
            switch (com) {
                case("login"):
                    Login login = (Login) current;
                    if(!checkLogin(login.getLogin())) throw new InvalidCommandArgumentException("Данного логина не сущствует");
                    if(!compare(getPassword(login.getLogin().trim()), login.getPassword().trim())) throw new InvalidCommandArgumentException("Неправильный пароль");
                    stmt = connection.createStatement();
                    ResultSet psw = stmt.executeQuery("select password from users where login = \'"+login.getLogin()+"\';");
                    if(psw.next())
                    return login;
                case("registration")://done
                    Login registration = (Login) current;
                    if(checkLogin(registration.getLogin())) throw new InvalidCommandArgumentException("Данный логин уже существует");
                    stmt = connection.createStatement();
                    stmt.execute("INSERT INTO users (login, password) VALUES (\'"+registration.getLogin().trim()+"\', \'"+registration.getPassword().trim()+"\');");
                    users.add(registration.getLogin());
                    return registration;
                case ("info")://done
                    Info info = (Info) current;
                    info.setCollection(collection);
                    return info;
                case ("show"): //done
                    Show show = (Show) current;
                    if (collection.getCollection() == null) throw new EmptyCollectionException();
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    show.setCollection(collection);
                    show.getResult();
                    return show;
                case ("add"): //done
                    Add add = (Add) current;
                    add.setPerson(addToBD(add.getPerson(), add.getLogin()));
                    collection.add(add.getPerson());
                    add.setCollection(collection);
                    return (add);
                case ("update_by_id"): //контроль
                    UpdateById updateById = (UpdateById) current;
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    if (!collection.checkId(updateById.getNewId()))
                        throw new InvalidCommandArgumentException("нет такого id");
                    if(!getOwner(updateById.getNewId()).equals(updateById.getLogin())) throw new AccessException();
                    collection.updateById(updateById.getNewId(), updateById.getUpdatePerson());
                    updateById.setCollection(collection);
                    return (updateById);
                case ("remove_by_id"): //контроль
                    RemoveById removeById = (RemoveById) current;
                    int id = removeById.getRemoveID();
                    if (collection.getCollection().isEmpty()) throw new EmptyCollectionException();
                    if (!collection.checkId(id)) throw new InvalidCommandArgumentException("нет такого id");
                    if(!getOwner(id).equals(removeById.getLogin())) throw new AccessException();
                    collection.removeById(id);
                    removeById.setCollection(collection);
                    return(removeById);
                case ("clear"): //контроль
                    Clear clear = (Clear) current;
                    collection.clear();
                    clear.setCollection(collection);
                    return(clear);
                case ("execute_script"):
                    ExecuteScript executeScript = (ExecuteScript) current;
                    executeScript.setCollection(collection);
                    return(executeScript);
                case ("remove_first"): //контроль
                    RemoveFirst removeFirst = new RemoveFirst();
                    if(!getOwner(collection.getCollection().firstElement().getId()).equals(removeFirst.getLogin())) throw new AccessException();
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
        }
        Mistake mistake = new Mistake();
        mistake.setMessage("Вы отправили не сущетсвующую команду");
        return mistake;
    }

    private String getCode(String psw) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bytes = md.digest(psw.getBytes());
        StringBuilder builder = new StringBuilder();
        for(byte b: bytes){
            builder.append(String.format("%02X ",b));
        }
        return builder.toString();
    }

    private boolean checkLogin(String login) throws SQLException {
        return users.contains(login);
    }

    private void getUsers() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rst = stmt.executeQuery("Select login from users;");
        while(rst.next()){
            users.add(rst.getString("login"));
        }
    }

    private boolean compare(String a, String b){
        if(a.length()!=b.length()) return false;
        for(int i=0;i<a.length(); i++){
            if(a.charAt(i)!=b.charAt(i)){
                System.out.println(a.charAt(i)+" "+b.charAt(i));
                return false;
            }
        }
        return true;
    }

    private String getPassword(String login) throws SQLException {
        Statement stm = connection.createStatement();
        login.trim();
        ResultSet psw = stm.executeQuery("select password from users where login = \'"+login+"\';");
        if(psw.next()) return psw.getString("password").trim();
        return " ";
    }

    private Person addToBD(Person p, String user) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("insert into persons values(nextval('getid'), " +
                "\'"+p.getName()+"\', " +
                p.getCoordinates().getX()+", "+
                p.getCoordinates().getY()+", "+
                "\'"+DataConverter.dateToString(p.getCreationDate())+"\', "+
                p.getHeight()+", "+
                p.getWeight()+", "+
                "\'"+p.getHairColor().toString()+"\', "+
                "\'"+p.getNationality().toString()+"\', "+
                p.getLocation().getX()+", "+
                p.getLocation().getY()+", "+
                p.getLocation().getZ()+", "+
                "\'"+user+"\'"+
                ");");
        Statement st = connection.createStatement();
        ResultSet res = st.executeQuery("select id from persons order by id desc limit 1;");
        if(res.next()){
            p.addId(Integer.parseInt(res.getString("id")));
        }
        return p;
    }

    private String getOwner(int id) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery("select user_creator from persons where id = "+id+";");
        res.next();
        return res.getString("user_creator").trim();
    }

    private PersonCollection readCollection() throws SQLException, ParameterException {
        PersonCollection result = new PersonCollection();
        Statement st = connection.createStatement();
        ResultSet res = st.executeQuery("select * from persons;");
        while(res.next()){
            Person p = new Person();
            Person.ReadParameter parameter = p. new ReadParameter();
            p.addId(parameter.convertToId(res.getString("id").trim()));
            p.addName(res.getString("name"));
            p.addCoordinates(parameter.convertToCX(res.getString("coordinate_x")), parameter.convertToCY(res.getString("coordinate_y")));
            p.addCreationDate(DataConverter.parseLocalDate(res.getString("creationdate")));
            p.addHeight(parameter.convertToHeight(res.getString("height")));
            p.addWeight(parameter.convertToWeight(res.getString("weight")));
            p.addColor(parameter.convertToColor(res.getString("hair_color")));
            p.addNationality(parameter.convertToCountry(res.getString("nationality")));
            p.addLocation(parameter.convertToLX(res.getString("location_x")), parameter.convertToLY(res.getString("location_y")), parameter.convertToLZ(res.getString("location_z")));
            result.add(p);
        }
        return result;
    }

}
