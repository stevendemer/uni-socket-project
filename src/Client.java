import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public Client(Socket socket, String username) throws IOException {

        try {
            this.socket = socket;

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.username = username;

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            this.writer.write(message);
            this.writer.newLine();
            this.writer.flush();

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
                String msgToSend = scanner.nextLine();
                this.writer.write(this.username + " " + msgToSend);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }


    public static void main(String[] args) throws IOException {

        try (Socket socket = new Socket("localhost", 9000); PrintWriter writer = new PrintWriter(new DataOutputStream(socket.getOutputStream()), true); BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));) {

            writer.println("Client connected !");
            String fromServer = "";
            String fromUser = "";

            while ((fromServer = reader.readLine()) != null) {

                System.out.println("[SERVER]: " + fromServer);

                if (fromServer.equals("bye")) {
                    break;
                }

                fromUser = userInput.readLine();

                if (fromUser != null) {
                    writer.println(fromUser);
                    System.out.println("[SENT TO SERVER]: " + fromUser);
                }

//                String input = userInput.readLine();

                // send the user's choice to the server
//                writer.println(fromUser);

                if ("exit".equalsIgnoreCase(fromUser)) {
                    System.out.println("Client is exiting...");
                    break;
                }

                // get server's response
                String response = reader.readLine();

                System.out.println("[SERVER]: " + response);

//                System.out.print("Server response: " + response);
            }

        } catch (UnknownHostException ex) {
            System.out.println("Server not found " + ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
