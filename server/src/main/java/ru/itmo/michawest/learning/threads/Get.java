package ru.itmo.michawest.learning.threads;

import ru.itmo.michawest.learning.ExecuteService;
import ru.itmo.michawest.learning.collection.PersonCollection;
import ru.itmo.michawest.learning.commands.Command;
import ru.itmo.michawest.learning.commands.Mistake;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.Semaphore;

public class Get implements Runnable {
    private Command command;
    private InetSocketAddress addressOfSender;
    private DatagramChannel channel;
    private Staps staps;
    private Execute execute;
    private ExecuteService executeService;
    private Semaphore sem;

    public Get(Staps staps) throws IOException {
        this.staps = staps;
        this.channel = staps.getChannel();
        this.execute = new Execute(staps);
        this.executeService = new ExecuteService(execute);
        this.sem = new Semaphore(1);
    }

    @Override
    public void run() {
        try{
            try{
                sem.acquire();
                staps.setPs(false);
                ByteBuffer buf = ByteBuffer.allocate(1024 * 1024);
                addressOfSender = (InetSocketAddress) channel.receive(buf);

                ByteArrayInputStream readbuf = new ByteArrayInputStream(buf.array());
                ObjectInputStream readOb = new ObjectInputStream(readbuf);
                staps.setAddressOfSender(addressOfSender);

                staps.setCommand((Command)readOb.readObject());
                System.out.println("Команда получена");
                executeService.run();
                sem.release();
            }catch(ClassNotFoundException e){
                command = new Mistake("Вы отправили объект не типа Command");
                staps.setCommand(command);
            } catch (InterruptedException e) {
                System.out.println("Сервер выключили");
            }
        } catch (IOException e) {
            command = new Mistake("Возниела ошибка при получении команды");
        }
    }

    public Staps getStaps(){
        return staps;
    }

    public void readCommand() throws IOException, ClassNotFoundException {
        ByteBuffer buf = ByteBuffer.allocate(1024 * 1024);
        addressOfSender = (InetSocketAddress) channel.receive(buf);
        ByteArrayInputStream readbuf = new ByteArrayInputStream(buf.array());
        ObjectInputStream readOb = new ObjectInputStream(readbuf);
        staps.setAddressOfSender(addressOfSender);
        staps.setCommand((Command)readOb.readObject());
    }

    public void closePool(){
        executeService.close();
    }

    public DatagramChannel getChannel(){
        return channel;
    }

}
