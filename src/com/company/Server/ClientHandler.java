package com.company.Server;

import com.company.Client.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private Socket socket;
    private String username;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Scanner scanner;

    public ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    @Override
    public void run() {
        try {
            dataOutputStream.writeUTF("Username?");
            username = dataInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Adding " + username + " to users");
        Server.getClients().add(new Client(username, socket.getPort(), socket.getRemoteSocketAddress().toString()));
        String recievedMessage;
        while (true) {
            try {
                recievedMessage = dataInputStream.readUTF();
                System.out.println(username + ": " + recievedMessage);
                for (Client client : Server.getClients()) {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
