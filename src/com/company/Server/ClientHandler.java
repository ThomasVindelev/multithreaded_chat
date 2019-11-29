package com.company.Server;

import com.company.Client.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {

    private Socket socket;
    private String username;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private int countHeartBeat = 0;
    private Pattern pattern = Pattern.compile("^[-a-zA-Z0-9_-]+");
    private boolean closeConnection = false;

    public ClientHandler(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    @Override
    public void run() {

        // handles username and validation of username
        try {
            boolean success = true;
            dataOutputStream.writeUTF("Write username: ");
            while(success) {

                success = false;
                username = dataInputStream.readUTF();

                // validates length of username
                if (username.length() > 12) {
                    dataOutputStream.writeUTF("J_ER 400: Too many characters. Max 12 char");
                    success = true;
                }
                // validates characters of username
                if (!pattern.matcher(username).matches()) {
                    dataOutputStream.writeUTF("J_ER 400: Unknown symbols used. User only letters, digits, ‘-‘ and ‘_’ allowed");
                    success = true;
                }

                // check of already existing username exists
                for (Client client : Server.getClients()) {
                    if (client.getUsername().equals(username)) {
                        success = true;
                        dataOutputStream.writeUTF("J_ER 400: This username is already taken - please try another one: ");
                    }
                }
            }
            dataOutputStream.writeUTF("J_OK");
            System.out.println("Adding " + username + " to users");
            StringBuilder listOfUsers = new StringBuilder("LIST << ");
            for (Client client: Server.getClients()) {
                listOfUsers.append(client.getUsername()).append(" ");
            }
            listOfUsers.append(">>");
            dataOutputStream.writeUTF(listOfUsers.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // adds new user to array list over all current users.
        Client newClient = new Client(username, socket.getPort(), socket.getRemoteSocketAddress().toString(), dataOutputStream);
        Server.getClients().add(newClient);

        // heartbeat thread. handles user heartbeat.
        Thread heartBeat = new Thread(() -> {
            boolean keepThreadAlive = true;
            while(keepThreadAlive) {
                countHeartBeat++;
                try {
                    Thread.sleep(1000);
                    System.out.println(countHeartBeat);
                    if(countHeartBeat == 8) {
                        System.out.println("disconnect user");
                        Server.getClients().remove(newClient);
                        keepThreadAlive = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        heartBeat.start();

        // handles recieved messages.
        String recievedMessage;
        while (!closeConnection) {
            try {
                recievedMessage = dataInputStream.readUTF();
                if(recievedMessage.equals("DATA "+ username + ": QUIT")) {
                    newClient.getDataOutputStream().writeUTF("QUIT");
                    closeConnection = true;
                    Server.getClients().remove(newClient);
                    break;
                } else if(recievedMessage.length() > 250) {
                    newClient.getDataOutputStream().writeUTF("J_ER 400: Too many characters. Max 250 Char ");
                } else if (recievedMessage.equals("IMAV")) {
                    countHeartBeat = 0;
                } else {
                    System.out.println(recievedMessage);
                    for (Client client : Server.getClients()) {
                        if (!client.getUsername().equals(username)) {
                            client.getDataOutputStream().writeUTF(recievedMessage);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeConnection = true;
            }
        }
    }
}
