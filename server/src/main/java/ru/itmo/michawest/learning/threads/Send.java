package ru.itmo.michawest.learning.threads;

import ru.itmo.michawest.learning.collection.PersonCollection;
import ru.itmo.michawest.learning.commands.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.Semaphore;

public class Send implements Runnable{
    private DatagramChannel channel;
    private InetSocketAddress addressOfSender;
    private Semaphore sem;
    private Staps staps;

    public Send(Staps staps){
        this.staps = staps;
        this.channel = staps.getChannel();
        this.addressOfSender = staps.getAddressOfSender();
        this.sem = new Semaphore(1);
    }

    @Override
    public void run() {
        try {
            sem.acquire();
            this.addressOfSender = staps.getAddressOfSender();
            ByteBuffer buf = writeCommand();
            channel.send(buf, addressOfSender);
            System.out.println("Команда отправлена");
            System.out.println(Thread.currentThread().getName());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        sem.release();
        Thread.currentThread().interrupt();
    }

    public ByteBuffer writeCommand() throws IOException {
        ByteArrayOutputStream writebuf = new ByteArrayOutputStream(1024*1024);
        ObjectOutputStream writeOb = new ObjectOutputStream(writebuf);
        writeOb.writeObject(staps.getResult());
        return ByteBuffer.wrap(writebuf.toByteArray());
    }

}
