import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {


    private static List<Account> accounts = new ArrayList<>();

    public Server() {
    }

    public static void main(String[] args) {

        int port = 9000;
        Server server = new Server();

        try (ServerSocket serverSocket = new ServerSocket(port); Socket clientSocket = serverSocket.accept(); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            String outputLine, inputLine;

            out.println("Pick a choice (1-6)");

            while ((inputLine = in.readLine()) != null) {
                inputLine = inputLine.trim().toLowerCase();

                if (inputLine.equalsIgnoreCase("exit")) {
                    out.println("Server closing...");
                    break;
                }

                System.out.println("[FROM CLIENT]: " + inputLine);

                switch (inputLine) {
                    case "1":
                        out.println("New account added !");
//                        accounts.add(new Account("Steven Demer", 9013));
                        showAccounts(out);

                        break;
                    case "2":
                        out.println("Picked 2");
                        break;
                    case "3":
                        out.println("Picked 3");
                        break;
                    case "4":
                        out.println("Picked 4");
                        break;
                    default:
                        out.println("Invalid option");
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);

                new Thread(clientHandler).start();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addAccount(String username) {
        int token = generateToken();
//        accounts.add(new Account(username, token));
    }

    public static void showAccounts(PrintWriter out) {
        for (Account account : accounts) {
            out.println("[USERNAME] = " + account.getUsername() + " [INBOX] = " + account.getMessages());
        }
    }


    private int generateToken() {
        int max = 999;
        int min = 1;
        return (int) (Math.random() * max + min);
    }

    private static class ClientHandler implements Runnable {

        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                while (true) {
                    // read the client's choice
                    int choice = in.read();
                    if (choice == -1) {
                        break;
                    }

                    String result = makeChoice(choice);

                    out.println(result);
                }

                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                clientSocket.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public String makeChoice(int choice) {
            return switch (choice) {
                case 1 -> "Option 1 selected";
                case 2 -> "Option 2 selected";
                case 3 -> "Option 3 selected";
                case 4 -> "Option 4 selected";
                default -> "Default option";
            };
        }
    }
}
