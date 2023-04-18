package courseudem.demo.server;

import java.io.File;

public class ServerLauncher {
    public final static int PORT = 1552;

    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}