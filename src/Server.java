package com.javarush.task.task30.task3008;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.Map;

public class Server {

    private static final Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message){
        try {
            for (Connection connection :
                    connectionMap.values()) {
                connection.send(message);
            }
        } catch (IOException e){
            System.out.println("Не смогли отправить сообщение");
        }
    }


    public static void main(String[] args) {
        System.out.println("ВВедите порт сервера");

        int port = ConsoleHelper.readInt();

        try (ServerSocket socket = new ServerSocket(port)){
            System.out.println("Сервер запущен");

            while (true){
                Thread thread = new Handler(socket.accept());
                thread.start();
            }
        } catch (Exception e){
            System.out.println("Произошла ошибка");;
        }
    }

    private static class Handler extends Thread{
        private Socket socket;

        public Handler (Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {

        }
    }
}
