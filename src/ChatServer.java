import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class ChatServer extends Thread {
    private static int PORTNUMBER = 5555;
    public static ArrayList<UserThread> socketList = new ArrayList<>();
    private static ServerSocket serverSocket;
    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORTNUMBER);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String name = in.readLine();
                UserThread t = new UserThread(clientSocket, name, socketList);
                t.start();
                for(UserThread u : socketList) u.out.println("new user:" + name);
                socketList.add(t);
                System.out.println("User: " + name + " connected");
            } catch (IOException e) {
                System.out.println("Error accepting new connection");
                e.printStackTrace();
            }
        }
    }
}

class UserThread extends Thread {
    public Socket s;
    public String name;
    PrintWriter out;
    BufferedReader in;
    ArrayList<UserThread> others;
    public UserThread(Socket s, String name, ArrayList<UserThread> list) throws IOException {
        this.s = s;
        this.name = name;
        out = new PrintWriter(s.getOutputStream(), true);
        out.println(name);
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        others = list;
        for(UserThread u : others) {
            if(u != this) out.println("new user:" + u.name);
        }
    }

    public void run() {
        while(true) {
            try {
                if(in.ready()) {
                    String input = in.readLine();
                    System.out.println("Message received from: " + name + " message: " + input);
                    for(UserThread u : others) {
                        u.out.println(name + ":" + input);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


