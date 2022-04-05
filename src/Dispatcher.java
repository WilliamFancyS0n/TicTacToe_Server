import java.io.IOException;
import java.net.*;

public class Dispatcher {
    static ServerSocket port;
    static {
        try {
            port = new ServerSocket(8787);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            while (true) {
                System.out.println("Server is awaiting new client...");
                Socket client = port.accept(); // accept a new client
                System.out.println("New client has connected");
                Server connection = new Server(client); // initialize a new server thread with our client
                connection.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } // end of dispatcher responsibilities
    }
}
