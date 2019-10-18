package com.company.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private String username;
    private int port = 5000;
    private String ip;

    public Client() {

    }

    public Client(String username, int port, String ip) {
        this.username = username;
        this.port = port;
        this.ip = ip;
    }

    public void clientConnection() {

        Scanner scanner = new Scanner(System.in);

        try {
            InetAddress ip = InetAddress.getByName("localhost");
            Socket socket = new Socket(ip, port);

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            Thread sendMsg = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){

                        String msg = scanner.nextLine();

                        try {
                            dataOutputStream.writeUTF(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

            Thread recieveMsg = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String msg = dataInputStream.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            sendMsg.start();
            recieveMsg.start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
