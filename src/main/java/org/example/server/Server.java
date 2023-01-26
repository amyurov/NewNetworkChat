package org.example.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final String LISTENING_MSG = "Server is listening";
    private static final String CLIENT_CONNECTED = "Client connected on ";
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    // Метод для прослушки сервера переданного порта
    public void listen(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Server starting on {}", port);

            while (!serverSocket.isClosed()) {
                log.info(LISTENING_MSG);
                System.out.println(LISTENING_MSG);

                Socket clientSocket = serverSocket.accept();

                log.info(CLIENT_CONNECTED + clientSocket.getRemoteSocketAddress());
                System.out.println(CLIENT_CONNECTED + clientSocket.getRemoteSocketAddress());
                // Как только к серверу подключился клиент, запускаем обработчик подключений в новом потоке
                threadPool.submit(() -> new ClientHandler(clientSocket).handlingConnection());
            }
        } catch (IOException e) {
            log.error("Ошибка в работе серверного сокета " + e);
        }
    }

    // Получение дефолтного порта
    protected int getDefaultPort() {
        return 8080;
    }
}
