import java.io.*;
import java.net.Socket;
import java.util.List;
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


        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {


            // send function code to the server
            out.writeInt(functionCode);
            out.flush();

            if (functionCode == 1) {
                String username = args[3];
                out.writeUTF(username);
                out.flush();

                // receive the token
                String token = in.readUTF();
                System.out.println("Generated token: " + token);
            } else if (functionCode == 2) {
                String token = args[3];

                out.writeUTF(token);
                out.flush();

                List<Account> accounts = (List<Account>) in.readObject();

                System.out.println("All accounts stored in the system: ");
                for (Account account : accounts) {
                    System.out.println(account.toString());
                }

            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
