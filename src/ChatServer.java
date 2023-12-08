import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ChatServer {

    private final List<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: Server.jar <port> ");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        new ChatServer().startServer(port);
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
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream()); ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());) {
                while (true) {
                    int functionCode = in.readInt();

                    if (functionCode == 1) {
                        String username = in.readUTF();

                        String response = createAccount(username);
                        out.writeUTF(response);
                        out.flush();
                    } else if (functionCode == 2) {
                        String token = in.readUTF();

                        Account account = findByToken(token);

                        if (account != null) {

                            StringBuffer buffer = new StringBuffer();

                            for (int i = 0; i < accounts.size(); ++i) {
                                buffer.append(i + ". " + accounts.get(i).getUsername() + "\n");
                            }
                            out.writeUTF(buffer.toString());
                        } else {
                            // user needs to be authenticated for this action
                            out.writeUTF("Invalid Auth Token");
                        }
                        out.flush();
                    } else if (functionCode == 3) {
                        String[] body = (String[]) in.readObject();

                        // deconstruct the params passed
                        String token = body[0];
                        String username = body[1];
                        String messageBody = body[2];

                        String response = sendMessage(username, messageBody, token);

                        out.writeUTF(response);
                        out.flush();
                    } else if (functionCode == 4) {
                        String token = in.readUTF();

                        String response = readInbox(token);

                        out.writeUTF(response);
                        out.flush();

                    } else if (functionCode == 5) {
                        String[] body = (String[]) in.readObject();

                        String token = body[0];
                        int messageId = Integer.parseInt(body[1]);

                        String response = readMessage(messageId, token);

                        out.writeUTF(response);
                        out.flush();

                    } else if (functionCode == 6) {
                        String[] body = (String[]) in.readObject();

                        String token = body[0];
                        String messageId = body[1];

                        String response = deleteMessage(Integer.parseInt(messageId), token);

                        out.writeUTF(response);
                        out.flush();
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        private String generateToken() {
            int max = 999;
            int min = 1;
            int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
            return String.valueOf(randomNum);
        }

        private String sendMessage(String recipient, String messageBody, String token) {
            StringBuffer buffer = new StringBuffer();

            Account author = findByToken(token);
            Account recipAccount = accounts.stream().filter(account -> recipient.equalsIgnoreCase(account.getUsername())).findAny().orElse(null);

            if (recipAccount == null) {
                buffer.append("User does not exist");
            } else if (author == null) {
                buffer.append("Invalid Auth Token");
            } else {
                recipAccount.addMessage(new Message(author.getUsername(), recipient, messageBody));
                buffer.append("OK");
            }
            return buffer.toString();
        }

        private String createAccount(String username) {

            StringBuffer buffer = new StringBuffer();

            // username must be alpha
            if (!username.matches("[a-zA-Z]+")) {

                buffer.append("Invalid username");
                return buffer.toString();
            }

            Account account = accounts.stream().filter(acc -> acc.getUsername().equalsIgnoreCase(username)).findAny().orElse(null);

            if (account != null) {
                buffer.append("Sorry, the user already exists");
                return buffer.toString();
            }
            String token = generateToken();
            accounts.add(new Account(username, token));
            buffer.append(token);
            return buffer.toString();
        }


        private String readInbox(String token) {

            StringBuffer buffer = new StringBuffer();
            Account account = findByToken(token);

            if (account == null) {
                buffer.append("Invalid Auth Token");
                return buffer.toString();
            }

            List<Message> inbox = account.getMessages();

            if (inbox.isEmpty()) {
                buffer.append("Inbox is empty");
                return buffer.toString();
            }

            for (Message message : inbox) {
                if (message.isRead()) {
                    buffer.append(message.getId() + ". from: " + message.getSender());
                } else {
                    buffer.append(message.getId() + ". from: " + message.getSender() + "*");
                }
            }

            return buffer.toString();
        }

        private String readMessage(int id, String token) {
            Account account = findByToken(token);
            StringBuffer buffer = new StringBuffer();

            if (account != null) {
                for (Message message : account.getMessages()) {
                    if (message.getId() == id) {
                        message.setRead(true);
                        buffer.append("( " + message.getSender() + " ) " + message.getBody());
                        return buffer.toString();
                    }
                }
                buffer.append("Message ID does not exist");
            } else {
                buffer.append("Invalid Auth Token");
            }
            return buffer.toString();
        }

        private String deleteMessage(int id, String token) {

            StringBuffer buffer = new StringBuffer();

            Account account = findByToken(token);

            if (account != null) {
                List<Message> inbox = account.getMessages();
                Message message = inbox.stream().filter(msg -> msg.getId() == id).findFirst().orElse(null);
                if (message == null) {
                    buffer.append("Message does not exist");
                    return buffer.toString();
                }
                inbox.remove(message);
                buffer.append("OK");
            } else {
                buffer.append("Invalid Auth Token");
            }
            return buffer.toString();
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

