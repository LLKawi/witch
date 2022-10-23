package me.soda.witch.server;

import me.soda.witch.server.handlers.CommandHandler;
import me.soda.witch.server.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static Server server;

    public static void main(String[] args) throws IOException {
        int port = 11451;
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        server = new Server(port);
        CommandHandler commandHandler = new CommandHandler();
        server.start();
        server.log("Port: " + server.getPort());
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = inputStream.readLine();
            if (commandHandler.handle(in, server)) break;
        }
    }
}