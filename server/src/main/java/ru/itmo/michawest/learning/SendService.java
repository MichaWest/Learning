package ru.itmo.michawest.learning;

import ru.itmo.michawest.learning.threads.Send;
import ru.itmo.michawest.learning.threads.Staps;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendService implements Runnable{
    private final ExecutorService pool;
    private Send send;

    public SendService(Send send) throws IOException {
        this.send = send;
        pool = Executors.newFixedThreadPool(1);
    }

    @Override
    public void run() {
        pool.submit(send);
    }

    public ExecutorService getPool(){
        return pool;
    }

    public void close(){
        pool.shutdown();
    }
}
