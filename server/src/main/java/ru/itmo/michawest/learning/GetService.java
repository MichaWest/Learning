package ru.itmo.michawest.learning;

import ru.itmo.michawest.learning.threads.Get;
import ru.itmo.michawest.learning.threads.Staps;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetService implements Runnable {
    private final ExecutorService pool;
    private final Get get;

    public GetService(Get get) throws IOException {
       this.get = get;
       pool = Executors.newFixedThreadPool(1);
    }

    @Override
    public void run() {
        pool.submit(get);
        pool.shutdown();
    }

    public void close(){
        get.closePool();
        pool.shutdown();
    }
}
