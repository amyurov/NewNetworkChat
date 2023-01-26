package org.example.client;

import org.example.simple.parser.SimpleFileSettingsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
// Клиент имеет два метода для приема (receiveMessage) и отправки (sendMessage) сообщений.

    // Статическое поле логера.
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        // Инициализируем файл settingsFile, который передадим в SimpleFileSettingsParser для чтения параметров (хост, порт)
        File settingsFile = new File("src/main/java/org/example/client/settings.txt");
        SimpleFileSettingsParser settingsParser = new SimpleFileSettingsParser(settingsFile);

        log.debug("Соединение с сервером...");
        // Подулючаемся к сокету
        try (Socket socket = new Socket(settingsParser.getHost(), settingsParser.getPort())) {
            log.debug("Соединение установлено {}", socket.getRemoteSocketAddress());

            // Запускаем метод отправки сообщений в параллельном потоке. Указываем этот поток как демон.
            Thread thread = new Thread(() -> sendMessage(socket));
            thread.setDaemon(true);
            log.debug("Запуск метода для выходного потока");
            thread.start();

            // Запускаем метод приема сообщений в мейне.
            log.debug("Запуск метода для входного потока");
            receiveMessage(socket);

        } catch (IOException e) {
            String errMsg = "Ошибка соединения с сервером";
            log.error("{} {}", errMsg, e);
            System.out.println(errMsg);
        }
    }

    // Метод отправки сообщений
    private static void sendMessage(Socket socket) {
        try (Scanner scanner = new Scanner(System.in);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String msg;
            while (!socket.isOutputShutdown()) {
                msg = scanner.nextLine();
                writer.write(msg);
                writer.newLine();
                writer.flush();

                if (msg.equals("/exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод примема сообщений
    private static void receiveMessage(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String incomingMessage;
            while (!socket.isClosed()) {
                incomingMessage = reader.readLine();
                log.info(incomingMessage);
                if (incomingMessage.equals("/exit")) {
                    System.out.println("You are disconnected from Server " + socket.getRemoteSocketAddress());
                    break;
                }

                System.out.println(incomingMessage);
            }
        } catch (IOException e) {
            System.out.println("Потерянно соединение с сервером " + e);
        }
    }
}
