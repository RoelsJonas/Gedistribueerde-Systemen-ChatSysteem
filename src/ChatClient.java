import javax.swing.*;
import java.awt.*;

public class ChatClient {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create and display the GUI
                ChatGUI chatApp = new ChatGUI();
                String name = chatApp.getUsername();
                System.out.println(name);
            }
        });
    }

}
