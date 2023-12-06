import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * An object of this class will be created for each new client request
 */
public class ClientHandler implements Runnable {

    BufferedReader reader;
    PrintWriter writer;

    final Socket socket;


    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private List<Account> accounts;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.accounts = new ArrayList<>();
        clientHandlers.add(this);

        broadCastMessage("New client just connected !");
    }

    private String getUsername(String[] params) {
        if (params.length == 2) {
            String username = params[1];
            return username;
        }
        return null;
    }


    public void broadCastMessage(String message) {
        this.writer.println(message);
    }

    private void listAccounts() {
        for (Account account : this.accounts) {
            this.writer.println("[USERNAME] = " + account.getUsername() + " [INBOX] = " + account.getMessages());
        }
    }

    private void createAccount(String username) {
        int max = 9999;
        int min = 1;

        if (this.accounts.contains(username)) {
            this.writer.println(username + " user already exists !");
        } else {
//            this.accounts.add(new Account(username, (int) Math.floor(Math.random() * (max - min + 1) + min)));
            this.writer.println("New user " + username + " created ");
        }
    }

    private void closeConnections() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getMessage() {
        String line = null;
        StringBuffer completeMessage = new StringBuffer();

        try {
            while ((line = this.reader.readLine()) != null) {
                completeMessage.append(line);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        return completeMessage.toString();
    }

    @Override
    public void run() {


        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            while (true) {

                String line = in.readLine();

                System.out.println("Input from client: " + line);

                if (line.equalsIgnoreCase("1")) {
                    out.println("Enter username: ");
                    String username = in.readLine();
                    System.out.println("Username from user: " + username);
                    out.println("New user " + username + " added to the list!");
                } else if (line.equalsIgnoreCase("2")) {
                } else {
                    out.println("Invalid command!");
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
