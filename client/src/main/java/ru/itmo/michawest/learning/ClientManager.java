package ru.itmo.michawest.learning;

import ru.itmo.michawest.learning.Input.FileInput;
import ru.itmo.michawest.learning.Input.InputAll;
import ru.itmo.michawest.learning.collection.PersonCollection;
import ru.itmo.michawest.learning.commands.*;
import ru.itmo.michawest.learning.data.Color;
import ru.itmo.michawest.learning.data.Country;
import ru.itmo.michawest.learning.data.Person;
import ru.itmo.michawest.learning.exception.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.Stack;

public class ClientManager {
    private final DatagramSocket channel;
    private final int serverPort;
    private boolean isRunning;
    private final Scanner console;
    private PersonCollection personCollection;
    private String userName;
    private String currentScriptFileName;
    private InputAll input;
    private static Stack<String> runFiles = new Stack<>();
    private String currentCommand;
    private final String[] commands = {"help", "info", "show", "add", "remove_by_id", "update_by_id", "clear", "save", "execute_script", "exit", "remove_first", "reorder", "history", "group_counting_by_nationality", "count_by_hair_color", "login", "registration"};

    public ClientManager(int sp, InputAll inp) throws IOException {
        serverPort = sp;
        isRunning = true;
        console = new Scanner(System.in);
        input = inp;
        this.channel = new DatagramSocket();
    }

    public void execute(){
        boolean register = false;
        String command;

        while(!register){
            System.out.println("Необходимо зайти в свой аккаунт.");
            System.out.println("Для входа в существующий введите login, для регистрации введите registration");
            try {
                command = console.nextLine().toLowerCase().trim();
                login(command);
                Command com = getAnswer();
                if(com.getNameOfCommand().equals("login")||com.getNameOfCommand().equals("registration")) register = true;
            }catch(SendCommandException | GetCommandException e){
                System.out.println(e.getMessage());
            } catch(ClassNotFoundException e){
                System.out.println("Пришел кривой ответ от сервера");
            } catch(NullPointerException e){
                System.out.println("Вы вели пустую строку. Введите еще");
            } catch(InvalidCommandArgumentException e){
                System.out.println(e.getMessage());
            } catch(AbsenceAnswerException e){
                System.out.println("Не удалось зарегистрироваться. Сервер не отправляет ответ. Попробуйте позже.\n" +
                        "Возможно сервер находится на другом порте");
                isRunning = false;
                register = true;
            } catch(IOException e){
                System.out.println("Возникла ошибка. Попробуйте еще раз. Чтобы выйти введите exit");
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Ошибка при кодирование пароля. Пишите разработчику. Он дурак. michelle_3");
                isRunning = false;
                register = true;
            }
        }

        while(isRunning){
            try {
                System.out.println(userName + ", введите команду. Чтобы получит список команд введите \"help\".\nЧтобы сменить аккаунт введите или \"login\", или \"registration\"");
                command = console.nextLine().toLowerCase().trim();
                System.out.println(command);
                runCommand(new CommandWrapper(command));
            }catch(SendCommandException | GetCommandException e){
                //e.printStackTrace();
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        channel.close();
    }

    //Отправляет серверу запрос на регистрацию или вход
    public void login(String cmd) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        String password;
        String log;
        System.out.println(cmd);
        switch(cmd){
            case("registration"):
                System.out.println("Введите логин");
                System.out.print("login:");
                log = console.nextLine().toLowerCase().trim();
                if(log==null||log.isEmpty()) throw new InvalidCommandArgumentException("Вы вели пустую строку");
                if(log.length()>40) throw new InvalidCommandArgumentException("Слишком длинный логин");

                System.out.println("Введите пароль");
                System.out.print("password:");
                password = console.nextLine().toLowerCase().trim();
                if(password==null||password.isEmpty()) throw new InvalidCommandArgumentException("Вы вели пустую строку");

                Login registration = new Login("registration");
                registration.setLogin(log);
                registration.setPassword(getCode(password));
                this.userName = log;

                //отмечаем, что это регистрация
                registration.setCollection(personCollection);
                sendCommand(registration);
                break;
            case("login"):
                System.out.println("Введите логин");
                System.out.print("login:");
                log = console.nextLine().toLowerCase().trim();
                if(log==null||log.isEmpty()) throw new InvalidCommandArgumentException("Вы вели пустую строку");
                if(log.length()>40) throw new InvalidCommandArgumentException("Слишком длинный логин");

                System.out.println("Введите пароль");
                System.out.print("password:");
                password = console.nextLine().toLowerCase().trim();
                if(password==null||password.isEmpty()) throw new InvalidCommandArgumentException("Вы вели пустую строку");

                Login login = new Login();
                login.setLogin(log);
                login.setPassword(getCode(password));
                this.userName = log;

                login.setCollection(personCollection);
                sendCommand(login);
                break;
            case("exit"):
                System.out.println("Программа прекращает работу");
                isRunning = false;
                break;
            default:
                throw new InvalidCommandArgumentException("Ввод был неправильный. Для входа в существующий введите login, для регистрации введите registration");
        }
    }

    public void sendCommand(Command command) throws SendCommandException {
        try {
            ByteArrayOutputStream writebuf = new ByteArrayOutputStream(1024 * 1024);
            ObjectOutputStream writeOb = new ObjectOutputStream(writebuf);
            writeOb.writeObject(command);
            ByteBuffer buf = ByteBuffer.wrap(writebuf.toByteArray());
            DatagramPacket packet = new DatagramPacket(buf.array(), buf.array().length, InetAddress.getByName("localhost"), serverPort);
            channel.send(packet);
            System.out.println("Команда отправлена. Порт "+ serverPort);
            writeOb.flush();
        }catch(IOException e){
            throw new SendCommandException();
        }
    }

    public Command getAnswer() throws IOException, ClassNotFoundException {
        try {

            byte[] buf = new byte[1024 * 1024];
            DatagramPacket recePacket = new DatagramPacket(buf, buf.length);
            channel.setSoTimeout(8000);
            channel.receive(recePacket);
            ByteArrayInputStream readbuf = new ByteArrayInputStream(buf);
            ObjectInputStream readOb = new ObjectInputStream(readbuf);
            Command result = (Command) readOb.readObject();

            personCollection = result.getCollection();
            result.getResult();
            //System.out.println(result);z
            return result;
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException();
        } catch( SocketTimeoutException e){
            throw new AbsenceAnswerException();
        }
    }

    private void runCommand(CommandWrapper command) throws IOException {
        try {
            if(!command.getCom().equals("exit")) {
                if (!hasCommand(command.getCom())) {
                    throw new NoSuchCommandException();
                }
                System.out.println("Start");
                processingCommand(command);
                getAnswer();
            }else{
                isRunning = false;
            }
        }catch (ParameterException e) {
            System.out.println(e.getMessage());
        } catch (CommandException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }catch(IOException e){
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Ошибка при кодирование пароля. Пишите разработчику. Он дурак. michelle_3");
            isRunning = false;
        }
    }

    public void processingCommand(CommandWrapper command) throws IOException, ClassNotFoundException, ParameterException, NoSuchAlgorithmException {
        String arg = command.getArg();
        switch(command.getCom()){
            case("registration"):
                login("registration");
                break;
            case("login"):
                login("login");
                break;
            case ("help"):
                Help help = new Help();
                help.setLogin(userName);
                help.setCollection(personCollection);
                sendCommand(help);
                break;
            case("info"):
                Info info = new Info();
                info.setLogin(userName);
                info.setCollection(personCollection);
                sendCommand(info);
                break;
            case("show"):
                Show show = new Show();
                show.setLogin(userName);
                show.setCollection(personCollection);
                sendCommand(show);
                break;
            case("add"):
                Add add = new Add();
                Person p = input.readPerson();
                add.setPerson(p);
                add.setLogin(userName);
                add.setCollection(personCollection);
                sendCommand(add);
                break;
            case("update_by_id"):
                UpdateById updateById = new UpdateById();
                int nid;
                if (arg == null || arg.equals("")) {
                    throw new MissedCommandArgumentException();
                }
                try {
                    nid = Integer.parseInt(arg);
                } catch (NumberFormatException e) {
                    throw new InvalidCommandArgumentException("id должен быть типа int");
                }
                updateById.setNewId(nid);
                updateById.setCollection(personCollection);
                updateById.setLogin(userName);
                sendCommand(updateById);
                break;
            case("remove_by_id"):
                RemoveById removeById = new RemoveById();
                int id;
                if (arg == null || arg.isEmpty()) {
                    throw new MissedCommandArgumentException();
                }
                try {
                    id = Integer.parseInt(arg);
                } catch (NumberFormatException e) {
                    throw new InvalidCommandArgumentException("id должен быть типа int");
                }
                removeById.setRemoveID(id);
                removeById.setCollection(personCollection);
                removeById.setLogin(userName);
                sendCommand(removeById);
                break;
            case("clear"):
                Clear clear = new Clear();
                clear.setCollection(personCollection);
                clear.setLogin(userName);
                sendCommand(clear);
                break;
            case("save"):
                Save save = new Save();
                save.setCollection(personCollection);
                save.setLogin(userName);
                sendCommand(save);
                break;
            case("execute_script"):
                ExecuteScript executeScript = new ExecuteScript();
                try {
                    if (arg == null || arg.isEmpty()) {
                        throw new MissedCommandArgumentException();
                    }
                    if (runFiles.contains(arg)) throw new RecursiveException();
                    runFiles.push(arg);
                    ClientManager process = new ClientManager(serverPort, new FileInput(arg));
                    process.fileMode(arg);
                    runFiles.pop();
                } catch (RecursiveException e) {
                    System.out.println(e.getMessage());
                }
                executeScript.setCollection(personCollection);
                executeScript.setLogin(userName);
                sendCommand(executeScript);
                break;
            case("exit"):
                Exit exit = new Exit();
                exit.setCollection(personCollection);
                exit.setLogin(userName);
                sendCommand(exit);
                isRunning = false;
                break;
            case("remove_first"):
                RemoveFirst removeFirst = new RemoveFirst();
                removeFirst.setCollection(personCollection);
                removeFirst.setLogin(userName);
                sendCommand(removeFirst);
                break;
            case("reorder"):
                Reorder reord = new Reorder();
                reord.setCollection(personCollection);
                reord.setLogin(userName);
                sendCommand(reord);
                break;
            case("history"):
                History history = new History();
                history.setCollection(personCollection);
                history.setLogin(userName);
                sendCommand(history);
                break;
            case("min_by_weight"):
                MinByWeight minByWeight = new MinByWeight();
                minByWeight.setCollection(personCollection);
                minByWeight.setLogin(userName);
                sendCommand(minByWeight);
                break;
            case("group_counting_by_nationality"):
                GroupCountingByNationality groupCountingByNationality = new GroupCountingByNationality();
                Country nation = input.readNationality();
                groupCountingByNationality.setNationality(nation);
                groupCountingByNationality.setCollection(personCollection);
                groupCountingByNationality.setLogin(userName);
                sendCommand(groupCountingByNationality);
                break;
            case("count_by_hair_color"):
                CountByHairColor countByHairColor = new CountByHairColor();
                Color color = input.readHairColor();
                countByHairColor.setColor(color);
                countByHairColor.setCollection(personCollection);
                countByHairColor.setLogin(userName);
                sendCommand(countByHairColor);
                break;
        }
        currentCommand = command.getCom();
    }

    private boolean hasCommand(String com) {
        for (String i : commands) {
            if (com.equals(i)) {
                return true;
            }
        }
        return false;
    }

    public void consoleExit(){
        System.out.println("Проблема соединения с сервером. Хотите переподключиться к нему?");
        Scanner scanner = new Scanner(System.in);
        if(scanner.nextLine().equals("Да")){
            execute();
        }else{
            System.out.println("Для выхода из приложения введите следующей командой exit.");
            Thread.currentThread().interrupt();
        }
    }

    public void fileMode(String path) throws IOException {
        currentScriptFileName = path;
        input = new FileInput(path);
        isRunning = true;
        while (isRunning && input.getScanner().hasNextLine()) {
            CommandWrapper cmd = input.readCommand();
            runCommand(cmd);
        }
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

}
