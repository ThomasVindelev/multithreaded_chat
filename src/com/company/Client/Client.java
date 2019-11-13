package com.company.Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private String username;
    private int port = 5000;
    private String ip;
    private DataOutputStream dataOutputStream;

    public Client() {

    }

    public Client(String username, int port, String ip, DataOutputStream dataOutputStream) {
        this.username = username;
        this.port = port;
        this.ip = ip;
        this.dataOutputStream = dataOutputStream;
    }

    public void clientConnection() {

        Scanner scanner = new Scanner(System.in);
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            Socket socket = new Socket(ip, port);

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            Thread sendMsg = new Thread(() -> {
                String msg = "";
                while(!msg.equals("quit")){
                    msg = scanner.nextLine();
                    try {
                        dataOutputStream.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread heartBeat = new Thread(() -> {
                while(true) {
                    try {
                        try {
                            dataOutputStream.writeUTF("heartBeat");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread recieveMsg = new Thread(() -> {
                String msg = "";
                while (!msg.equals("quit")) {
                    if(msg.equals("Welcome")) {
                        heartBeat.start();
                    }
                    try {
                        msg = dataInputStream.readUTF();
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

    public String getUsername() {
        return username;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }
}
