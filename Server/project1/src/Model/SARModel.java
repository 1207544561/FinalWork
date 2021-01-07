package Model;

public class SARModel {
    String Sender, Receiver, Message;
    public SARModel(String Sender, String Receiver, String Message) {
        this.Sender = Sender;
        this.Receiver = Receiver;
        this.Message = Message;
    }

    public String getSender() {
        return Sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public String getMessage() {
        return Message;
    }
}
