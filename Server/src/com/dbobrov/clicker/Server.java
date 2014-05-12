package com.dbobrov.clicker;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private final Robot robot;
    private final ExecutorService workers;
    private final AtomicInteger clientCount;
    private final ServerSocket serverSocket;
    private final Thread serverThread;
    private final int maxClients;

    private volatile boolean isRunning;

    public Server(int port, int maxClients) throws AWTException, IOException {
        robot = new Robot();
        workers = Executors.newFixedThreadPool(maxClients);
        clientCount = new AtomicInteger(0);
        serverSocket = new ServerSocket(port);
        serverThread = new Thread(this);
        isRunning = true;
        serverThread.start();
        this.maxClients = maxClients;
    }


    @Override
    public void run() {
        while (isRunning) {
            try {
                Socket client = serverSocket.accept();
                handleConnection(client);
            } catch (IOException e) {
                if (isRunning) {
                    System.err.println("accept() failed: " + e);
                }
            }
        }
    }

    public void interrupt() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverThread.interrupt();
    }

    private void handleConnection(Socket client) {
        if (clientCount.incrementAndGet() > maxClients) {
            clientCount.decrementAndGet();
            try {
                client.close();
            } catch (IOException ignore) {
            }
            return;
        }

        workers.submit(new Worker(client));
    }

    private class Worker implements Runnable {
        private final Socket client;

        private Worker(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                while (!client.isInputShutdown()) {
                    String line = reader.readLine();
                    handleMessage(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clientCount.decrementAndGet();
            }
        }

        private void handleMessage(String message) {
            try {
                switch (Keys.valueOf(message)) {
                    case LEFT:
                        robot.keyPress(KeyEvent.VK_LEFT);
                        robot.keyRelease(KeyEvent.VK_LEFT);
                        break;
                    case RIGHT:
                        robot.keyPress(KeyEvent.VK_RIGHT);
                        robot.keyRelease(KeyEvent.VK_RIGHT);
                        break;
                    default:
                        System.err.println("Unhandled key: " + message);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid message: " + message);
            }
        }
    }
}
