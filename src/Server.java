package com.javarush.task.task30.task3008;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NavigableMap;
import java.util.concurrent.*;
import java.util.Map;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message) {
        try {
            for (Connection connection :
                    connectionMap.values()) {
                connection.send(message);
            }
        } catch (IOException e) {
            System.out.println("Не смогли отправить сообщение");
        }
    }


    public static void main(String[] args) {
        System.out.println("ВВедите порт сервера");

        int port = ConsoleHelper.readInt();

        try (ServerSocket socket = new ServerSocket(port)) {
            System.out.println("Сервер запущен");

            while (true) {
                Thread thread = new Handler(socket.accept());
                thread.start();
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка");
            ;
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST, "Ввведите ваше имя"));

                Message message = connection.receive();
                if (message.getType()!=MessageType.USER_NAME){
                    ConsoleHelper.writeMessage("Это не имя");
                    continue;
                }

                String userName = message.getData();

                if (userName.isEmpty()){
                    ConsoleHelper.writeMessage("Попытка подключения без имени");
                    continue;
                }

                if (connectionMap.containsKey(userName)){
                    ConsoleHelper.writeMessage("Подключение по старому именю");
                    continue;
                }

                connectionMap.put(userName, connection);

                connection.send(new Message(MessageType.NAME_ACCEPTED));

                return userName;
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException{
            for (String name :
                    connectionMap.keySet()) {
                if (name.equals(userName)){
                    continue;
                }
                connection.send(new Message(MessageType.USER_ADDED, name));
            }
        }
    }
}
