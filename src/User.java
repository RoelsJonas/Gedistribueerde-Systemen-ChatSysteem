import java.net.Socket;

public class User {
    public Socket s;
    public String name;




    public User(Socket s, String name) {
        this.s = s;
        this.name = name;
    }
}
