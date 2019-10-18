package com.company.Server;

import com.company.Client.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private ServerSocket server = null;
    private static List<Client> clients = new ArrayList<>();

    public void acceptClient() {
        try {
            System.out.println("Waiting for client...");
            server = new ServerSocket(5000);
            while (true) {
                Socket socket = server.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                ClientHandler clientHandler = new ClientHandler(socket, dataInputStream, dataOutputStream);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Client> getClients() {
        return clients;
    }

}
