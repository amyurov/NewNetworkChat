package org.example.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

// Класс обработчик подключений
public class ClientHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    // InMemory БД подключенных клиентов, реализована на CopyOnWriteArrayList. Запись в нее происходит только при
    // успешном подключении нового клиента.
    private static final CopyOnWriteArrayList<ClientHandler> connectedClients = new CopyOnWriteArrayList<>();
    private static final String ENTER_NAME = "Please enter the nickname:";
    private static final String WELCOME_MESSAGE = "Welcome! You can start chating!";
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final String name;

    // Конструктор принимает сокет, переданный сервером. Интсанс этого класса как бы является клиентом.
    // Или, можно сказать, что у каждого клиента свой обработчик на сервере.
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            // Далее формируются потоки ввода-вывода для данного подключения (клиента)
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Сообщение клиенту с просьбой ввести имя
            messageToClient(ENTER_NAME);

            // Имя принимается, только если оно уникально.
            String assumedName = reader.readLine();
            while (!nameAvailableCheck(assumedName)) {
                messageToClient(assumedName + " already exist. Please take a different name ");
                assumedName = reader.readLine();
            }
            this.name = assumedName;

            //После проверки имени объект подключения (клиент) добавляется в БД
            connectedClients.add(this);
            // Сообщение оповещающее о том, что можно начинать общение в чате
            messageToClient(WELCOME_MESSAGE);
            // Сообщение для всех, о номов подключении
            broadcastMessageFromServer(name + " join the chat");
            log.info("User {} connected", name);
        } catch (IOException e) {
            log.error("Ошибка при создании обработчика подключения {} {}", socket.getRemoteSocketAddress(), e);
            throw new RuntimeException(e);
        }
    }

    //Метод проверяет уникальность имени
    private boolean nameAvailableCheck(String name) {
        for (ClientHandler client : connectedClients) {
            if (client.name.equals(name)) return false;
        }
        return true;
    }

    // Главный метод, который обрабатывает подключение (клиента)
    public void handlingConnection() {
        try {
            String messageFromClient;
            while (!socket.isClosed()) {
                // Принимает сообщения от клиента
                messageFromClient = reader.readLine();

                if (messageFromClient.equals("/exit")) {
                    messageToClient(messageFromClient);
                    disconnect();
                    log.info("User {} has been disconnected", name);
                    break;
                }

                log.info("{}: {}", name, messageFromClient);
                // Отправляет сообщение от клиента всем, кто подключен. Т.Е. всем кто находится в БД
                broadcastMessage(messageFromClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для отправки сообщения клиенту. (инстансу этого класса)
    private void messageToClient(String msg) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для отправки сообщения всем подключенным клиентам
    private void broadcastMessage(String msg) {
        for (ClientHandler client : connectedClients) {
            try {
                if (!client.name.equals(name)) {
                    client.writer.write(name + ": " + msg);
                } else {
                    client.writer.write("You: " + msg);
                }
                client.writer.newLine();
                client.writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Метод отправляет оповещения для всех, кроме this. Например сообщение о том что данный клиент вышел из чата.
    private void broadcastMessageFromServer(String msg) {
        for (ClientHandler client : connectedClients) {
            try {
                if (!client.name.equals(name)) {
                    client.writer.write("SERVER: " + msg);
                    client.writer.newLine();
                    client.writer.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Метод удаляет инстанс из коллекции, и закрывает все ресурсы.
    private void disconnect() {
        try {
            if (connectedClients.remove(this)) {
                broadcastMessageFromServer(name + " disconnected from chat");
                reader.close();
                writer.close();
                while (!socket.isBound()) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

