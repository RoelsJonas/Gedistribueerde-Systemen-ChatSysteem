import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatGUI {
    private JFrame frame;
    private JPanel usernamePanel;
    private JTextField usernameField;
    private JPanel chatPanel;
    private JTextArea messageArea = new JTextArea();
    private JTextField textField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private String enteredUsername;
    private Socket echoSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;
    public ArrayList<String> socketList;

    public ChatGUI() {
        try {
            echoSocket = new Socket("localhost", 5555);
            out =
                    new PrintWriter(echoSocket.getOutputStream(), true);
            in =
                    new BufferedReader(
                            new InputStreamReader(echoSocket.getInputStream()));
            stdIn =
                    new BufferedReader(
                            new InputStreamReader(System.in));

            Listener l = new Listener(echoSocket, messageArea, userListModel);
            l.start();
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + "localhost");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    "localhost");
            System.exit(1);
        }

        socketList = new ArrayList<>();
        frame = new JFrame("Chat App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        // Create the username input panel
        usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout());
        JLabel usernameLabel = new JLabel("Enter your username:");
        usernameField = new JTextField(20);
        JButton usernameSubmitButton = new JButton("Submit");

        usernameSubmitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enteredUsername = usernameField.getText();

                out.println(enteredUsername);
                System.out.println("Username: " + enteredUsername);
                if (!enteredUsername.isEmpty()) {
                    showGroupChat(enteredUsername);
                }
            }
        });

        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        usernamePanel.add(usernameSubmitButton);

        frame.add(usernamePanel, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    private void showGroupChat(String username) {
        // Remove the username input panel
        frame.remove(usernamePanel);
        frame.revalidate();

        // Create the group chat panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        // Message display area
//        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        // Text input field and send button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        textField = new JTextField();
        sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                if (!message.isEmpty()) {
//                    appendMessage(username + ": " + message);
                    textField.setText("");
                }
                out.println(message);
                System.out.println(message);
            }
        });

        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // User list
//        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(150, 0));


        for (String name : socketList) {
            userListModel.addElement(name);
        }

        chatPanel.add(messageScrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        chatPanel.add(userListScrollPane, BorderLayout.WEST);

        // Add the current user to the user list
        userListModel.addElement(username);

        frame.add(chatPanel, BorderLayout.CENTER);
        frame.revalidate();
    }

    private void appendMessage(String message) {
        messageArea.append(message + "\n");

    }

    public String getUsername() {
        return enteredUsername;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create and display the GUI
                ChatGUI chatApp = new ChatGUI();
            }
        });
    }
}


class Listener extends Thread {
    Socket s;
    PrintWriter out;
    BufferedReader in;
    JTextArea messageArea;
    DefaultListModel<String> userListModel;
    public Listener(Socket s, JTextArea jta, DefaultListModel<String> ulm) throws IOException {
        this.s = s;
        out = new PrintWriter(s.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        messageArea = jta;
        userListModel = ulm;
    }

    public void run() {
        while(true) {
            try {
                if(in.ready()) {
                    String input = in.readLine();
                    String[] parts = input.split(":");
                    if(parts[0].equals("new user")) {
                        // TODO: user aan lijst toevoegen
                        userListModel.addElement(parts[1]);
                    }
                    else if(parts.length >= 2) {
                        // TODO: logica als bericht ontvangen wordt
                        System.out.println("New message from: " + parts[0] + " received: " + parts[1]);
                        messageArea.append(parts[0] + ": " + parts[1] + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
