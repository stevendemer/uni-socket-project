import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Scanner;

public class ChatClient {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java client <ip> <port> <fn> <args>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int functionCode = Integer.parseInt(args[2]);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Function code " + functionCode);


        try (Socket socket = new Socket(host, port); ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream in = new ObjectInputStream(socket.getInputStream());) {


            // send function code to the server
            out.writeInt(functionCode);
            out.flush();

            if (functionCode == 1) {
                String username = args[3];
                out.writeUTF(username);
                out.flush();

                // receive the token
                String result = in.readUTF();
                System.out.println(result);
            } else if (functionCode == 2) {
                String token = args[3];

                out.writeUTF(token);
                out.flush();

//                Object serverResponse = in.readObject();
                String serverResponse = in.readUTF();

//                List<Account> accounts = (List<Account>) serverResponse;

//                for (int i = 0; i < accounts.size(); ++i) {
//                    // print all the accounts registered
//                    System.out.println(i + ". " + accounts.get(i).getUsername());
//                }
                System.out.println(serverResponse);
            } else if (functionCode == 3) {
                if (args.length < 6) {
                    System.out.println("Usage: <ip> <port> 3 <token> <recipient> <message_body>");
                    return;
                }

                String token = args[3];
                String recipient = args[4];
                String messageBody = args[5];

                String[] body = new String[]{token, recipient, messageBody};

                out.writeObject(body);
                out.flush();

                String serverResponse = in.readUTF();

                System.out.println(serverResponse);

            } else if (functionCode == 4) {
                String token = args[3];
                out.writeUTF(token);
                out.flush();

                String serverResponse = in.readUTF();

                System.out.println(serverResponse);

            } else if (functionCode == 5) {
                String token = args[3];
                String messageId = args[4];

                String[] body = new String[]{token, messageId};

                out.writeObject(body);
                out.flush();

                String response = in.readUTF();

                System.out.println(response);

            } else if (functionCode == 6) {
                String token = args[3];
                String messageId = args[4];

                String[] body = new String[]{token, messageId};

                out.writeObject(body);
                out.flush();

                String response = in.readUTF();

                System.out.println(response);

            } else {
                out.writeUTF("Invalid option");
                out.flush();
            }
        } catch (OptionalDataException ex) {
            System.out.println("Error reading object from the server : " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
