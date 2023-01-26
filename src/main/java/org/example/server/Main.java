package org.example.server;

import org.example.simple.parser.SimpleFileSettingsParser;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        // Инициализируем файл fileSettings, который передадим в SimpleFileSettingsParser для чтения параметров (порт)
        final File fileSettings = new File("src/main/java/org/example/server/settings.txt");
        SimpleFileSettingsParser settingsParser = new SimpleFileSettingsParser(fileSettings);

        Server server = new Server();

        // Если файл не получается прочитать, то запускаем сервер на дефолтном порту
        if (fileSettings.canRead()) {
            server.listen(settingsParser.getPort());
        } else {
            System.out.println("Can't read file settings.txt.\r\nServer starting on default port:8080");
            server.listen(server.getDefaultPort());
        }

    }
}
