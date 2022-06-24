package ru.itmo.michawest.learning;

import ru.itmo.michawest.learning.threads.Execute;
import ru.itmo.michawest.learning.threads.Staps;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecuteService implements Runnable{
    private final ExecutorService pool;
    //private final DatagramChannel channel;
    private Execute execute;

    public ExecuteService(Execute execute) throws IOException {
        //channel = execute.getStaps().getChannel();
        this.execute = execute;
        pool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        pool.submit(execute);
    }

    public ExecutorService getPool(){
        return pool;
    }

    public void close(){
        execute.closePool();
        pool.shutdown();
    }
}
