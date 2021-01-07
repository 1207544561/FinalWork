package Model;

public class RealMessageModel extends UserModel {
    private String Message, Group;
    public RealMessageModel(String UserName, String Password, String Name, String Email, String ImgPath, String Group, String Message) {
        super(UserName, Password, Name, Email, ImgPath);
        this.Message = Message;
        this.Group = Group;
    }

    public String getMessage() {
        return Message;
    }

    public String getGroup() {
        return Group;
    }
}
