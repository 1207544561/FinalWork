package Model;

public class MessgeModel {
    private String User, Message, ImgPath;
    public MessgeModel(String User, String Message, String ImgPath) {
        this.User = User;
        this.Message = Message;
        this.ImgPath = ImgPath;
    }

    public String getUser() {
        return User;
    }

    public String getMessage() {
        return Message;
    }

    public String getImgPath() {
        return ImgPath;
    }
}
