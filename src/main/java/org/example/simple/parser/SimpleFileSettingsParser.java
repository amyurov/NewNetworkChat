package org.example.simple.parser;

import java.io.*;

public class SimpleFileSettingsParser {

    private static final String PROPERTY_PORT = "set_port";
    private static final String PROPERTY_HOST = "set_host";

    private final File settingsFile;

    public SimpleFileSettingsParser(File settingsFile) {
        this.settingsFile = settingsFile;
    }

    public int getPort() {
        try(BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {

            String param = "";
            while (!param.startsWith(PROPERTY_PORT)) {
                param = reader.readLine();
            }

            return Integer.parseInt(param.split("=")[1].trim());

        } catch (FileNotFoundException e) {
            System.out.println(e);
            return 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHost() {
        try(BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {

            String param = "";
            while (!param.startsWith(PROPERTY_HOST)) {
                param = reader.readLine();
            }

            return param.split("=")[1].trim();

        } catch (FileNotFoundException e) {
            System.out.println(e);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
