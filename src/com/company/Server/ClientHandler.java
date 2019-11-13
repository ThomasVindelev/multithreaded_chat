package com.company.Server;

import com.company.Client.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private String username;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private int countHeatBeat = 0;

    public ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    @Override
    public void run() {

        Thread heartBeat = new Thread(() -> {
            while(true) {
                countHeatBeat++;
                try {
                    Thread.sleep(1000);
                    if(countHeatBeat == 60) {
                        System.out.println("disconnect user");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        heartBeat.start();

        try {
            boolean success = true;
            dataOutputStream.writeUTF("Username?");
            while(success) {
                success = false;
                username = dataInputStream.readUTF();
                for (Client client : Server.getClients()) {
                    if (client.getUsername().equals(username)) {
                        success = true;
                    }
                }
                if (success) {
                    dataOutputStream.writeUTF("This username is already taken - please try another one: ");
                }
            }
            dataOutputStream.writeUTF("Welcome");
            System.out.println("Adding " + username + " to users");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Client newClient = new Client(username, socket.getPort(), socket.getRemoteSocketAddress().toString(), dataOutputStream);
        Server.getClients().add(newClient);
        String recievedMessage;
        while (true) {
            try {
                recievedMessage = dataInputStream.readUTF();
                if(recievedMessage.equals("quit")) {
                    newClient.getDataOutputStream().writeUTF("quit");
                    Server.getClients().remove(newClient);
                    break;
                }
                System.out.println(username + ": " + recievedMessage);
                for (Client client : Server.getClients()) {
                    System.out.println("Hej");
                    if (!client.getUsername().equals(username)) {
                        client.getDataOutputStream().writeUTF(username + ": " + recievedMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
