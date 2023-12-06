import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private List<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {

        new ChatServer().startServer(8000);
    }


    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

                // handle client
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {

        private final Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;


        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ) {
                while (true) {
                    int functionCode = in.readInt();

                    if (functionCode == 1) {
                        String username = in.readUTF();
                        String token = "1024";
                        accounts.add(new Account(username, token));

                        // return the generated token to the client
                        out.writeUTF(token);
                        out.flush();
                    } else if (functionCode == 2) {
                        String token = in.readUTF();

                        Account account = findByToken(token);

                        if (account != null) {

                            out.writeObject(accounts);
//                            out.writeUTF("[USERNAME] = " + account.getUsername() + " [INBOX] = " + account.getMessages().toString());
                            out.flush();
                        } else {
                            out.writeUTF("No such account found !");
                            out.flush();
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private Account findByToken(String token) {
            for (Account account : accounts) {
                if (account.getAuthToken().equalsIgnoreCase(token)) {
                    return account;
                }
            }
            return null;
        }
    }
}

