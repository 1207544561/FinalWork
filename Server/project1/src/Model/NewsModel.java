package Model;

public class NewsModel {
    private String News, UserName, Name, ImgPath;
    public NewsModel(String News, String UserName, String Name, String ImgPath) {
        this.News = News;
        this.UserName = UserName;
        this.Name = Name;
        this.ImgPath = ImgPath;
    }

    public String getNews() {
        return News;
    }

    public String getUserName() {
        return UserName;
    }

    public String getImgPath() {
        return ImgPath;
    }

    public String getName() {
        return Name;
    }
}
