import java.io.Serializable;

public class Message implements Serializable {

    private static int counterID = 0;
    private boolean isRead;
    private String sender;
    private String receiver;
    private String body;

    private final int id;

    public Message(boolean isRead, String sender, String receiver, String body) {
        this.isRead = isRead;
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
        this.id = ++counterID;
    }

    public Message(String sender, String receiver, String body) {
        this(false, sender, receiver, body);
    }

    public boolean isRead() {
        return isRead;
    }

    public int getId() {
        return id;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message [ " +
                "isRead=" + isRead +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", body='" + body + '\'' +
                ", id='" + id + '\'' +
                ']';
    }
}
