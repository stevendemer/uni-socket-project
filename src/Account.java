import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {

    private String username;
    private String authToken;
    private List<Message> messages;


    public Account(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
        this.messages = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message newMessage) {
        messages.add(newMessage);
    }

    @Override
    public String toString() {
        return "Account [ " + "username='" + username + '\'' + ", authToken=" + authToken + ", messages=" + messages + '}';
    }

}
