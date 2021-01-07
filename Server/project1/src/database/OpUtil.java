package database;

import Model.MessgeModel;
import Model.NewsModel;
import Model.SARModel;
import Model.UserModel;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.FetchResult;
import com.mysql.cj.xdevapi.Result;
import com.sun.org.apache.xalan.internal.res.XSLTErrorResources_zh_TW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.xml.internal.bind.v2.runtime.Name;
import jdk.nashorn.internal.objects.annotations.Getter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.rmi.server.UnicastServerRef;

import javax.jws.soap.SOAPBinding;
import javax.print.attribute.standard.PresentationDirection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OpUtil extends DButil {
    private Connection connection = null;
    private PreparedStatement statement = null;

    public List<NewsModel> getOwnNews(String username) {
        List<NewsModel> list = new ArrayList<>();
        String sql = "select * from news where binary username=?";
        try {
            connection = GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String Name = resultSet.getString("name");
                String UserName = resultSet.getString("username");
                String News = resultSet.getString("newstext");
                String ImgPath = resultSet.getString("imagepath");
                NewsModel bean = new NewsModel(News, UserName, Name, ImgPath);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public void updateNews(NewsModel model) {
        String sql = "insert into news (username,name,newstext,imagepath) values (?,?,?,?)";
        try {
            connection = GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, model.getUserName());
            statement.setString(2, model.getName());
            statement.setString(3, model.getNews());
            statement.setString(4, model.getImgPath());
            statement.executeUpdate();
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<NewsModel> getNews() {
        List<NewsModel> list = new ArrayList<>();
        String sql = "select * from news";
        try {
            connection = GetConnection();
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String Name = resultSet.getString("name");
                String UserName = resultSet.getString("username");
                String News = resultSet.getString("newstext");
                String ImgPath = resultSet.getString("imagepath");
                NewsModel bean = new NewsModel(News, UserName, Name, ImgPath);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<SARModel> getallmessage(String UserName) {
        List<SARModel> list = new ArrayList<>();
        List<String> NameList = new ArrayList<>();
        String sql = "select * from user_talk_table where send_user=?";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) { //获取所有发过消息用户的用户名
//                String Sender = resultSet.getString("send_user");
                String Receiver = resultSet.getNString("res_user");
//                String Message = resultSet.getString("message");
//                SARModel SAR = new SARModel(Sender, Receiver, Message);
//                list.add(SAR);
                if(!NameList.contains(Receiver)) NameList.add(Receiver);
            }

            for(int i = 0; i < NameList.size(); i++) { //遍历姓名集合取出所有聊天记录
                String Name = NameList.get(i);
                String sqlx = "select * from user_talk_table where (send_user=? and res_user=?) or (send_user=? and res_user=?);";
                statement = connection.prepareStatement(sqlx);
                statement.setString(1, UserName);
                statement.setString(2, Name);
                statement.setString(3, Name);
                statement.setString(4, UserName);
                ResultSet resultSet1 = statement.executeQuery();
                while (resultSet1.next()) {
                    String Sender = resultSet1.getString("send_user");
                    String Receiver = resultSet1.getNString("res_user");
                    String Message = resultSet1.getString("message");
                    SARModel SAR = new SARModel(Sender, Receiver, Message);
                    list.add(SAR);
                }
            }


            this.CloseResultSet(resultSet);
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public String GetGroup(String UserName, String FriendName) {
        String sql = "select * from user_friend_table where username=? and friendname=?";
        String Result = null;
        try {
            connection = GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            statement.setString(2, FriendName);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Result = resultSet.getString("friendgroup");
            }
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result;
    }

    public List<String> QueryFriendList(String UserName) {
        List<String> list = new ArrayList<>();
        String sql = "select * from user_friend_table where username=?";
        try {
            connection = GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String name = resultSet.getString("friendname");
                list.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public void SaveMsg(String Sender, String Receiver, String Message) {
        String sql = "insert into user_talk_table (send_user,res_user,message) values (?,?,?)";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, Sender);
            statement.setString(2, Receiver);
            statement.setString(3, Message);
            statement.executeUpdate();
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeGroup(String UserName, String FriendName, String NewGroup) {
        String sql = "update user_friend_table set friendgroup=? where username=? and friendname=?";
        try {
             connection = this.GetConnection();
             statement = connection.prepareStatement(sql);
             statement.setString(1, NewGroup);
             statement.setString(2, UserName);
             statement.setString(3, FriendName);
             statement.executeUpdate();
             this.CloseStatement(statement);
             this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AddFriend(String User, String Friend) {
        String sql = "insert into user_friend_table (username,friendname,friendgroup) values (?,?,?)";
        try {
            String group = "我的好友";
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, User);
            statement.setString(2, Friend);
            statement.setString(3, group);
            statement.executeUpdate();
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public JSONArray GetMessage(String Sender, String Receiver, String SenderImg, String ReceiverImg) {
        JSONArray list = new JSONArray();
        String sql = "select * from user_talk_table where (send_user=? and res_user=?) or (send_user=? and res_user=?)";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, Sender);
            statement.setString(2, Receiver);
            statement.setString(3, Receiver);
            statement.setString(4, Sender);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String SendUser = resultSet.getString("send_user");
                String Message = resultSet.getString("message");
                JSONObject bean = new JSONObject();
                if(SendUser.equals(Sender)) {
                    bean.put("user", SendUser);
                    bean.put("message", Message);
                    bean.put("imgPath", SenderImg);
//                    MessgeModel sen = new MessgeModel(SendUser, Message, SenderImg);
                } else {
                    bean.put("user", Receiver);
                    bean.put("message", Message);
                    bean.put("imgPath", ReceiverImg);
//                    MessgeModel sen = new MessgeModel(Receiver, Message, ReceiverImg);
                }
                list.add(bean);

            }
            this.CloseResultSet(resultSet);
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public String getUserInformation(String UserName, String selection) {
        String sql = "select * from user where username=?";
        String ImgPath = null;
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ImgPath = resultSet.getString(selection);
            }
            this.CloseResultSet(resultSet);
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ImgPath;
    }

    public String GetNowGroup(String UserName, String FriendName) {
        String sql = "select * from user_friend_table where username=? and friendname=?";
        String result = null;
        try {
            connection = GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            statement.setString(2, FriendName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result = resultSet.getString("friendgroup");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public UserModel GetUser(String UserName) {
        UserModel user = null;
        String sql = "select * from user where username=?";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String Password = resultSet.getString("password");
                String Name = resultSet.getNString("name");
                String ImgPath = resultSet.getString("imagepath");
                String Email = resultSet.getString("email");
                user = new UserModel(UserName, Password, Name, Email, ImgPath);
            }
            this.CloseResultSet(resultSet);
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }


    public List<String> GetFriendGroup(String UserName) {
        List<String> list = new ArrayList<>();
        String sql = "select * from user_group_table where username=?";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("usergroup"));
            }
            this.CloseResultSet(resultSet);
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public  JSONObject GetFriendList(String UserName) {
        List<String[]> list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        ResultSet resultSet = null;
        Iterator<String[]> it = null;
        String sql = "select * from user_friend_table where username=?";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String friendname = resultSet.getString("friendname");
                String friendgroup = resultSet.getString("friendgroup");
//                System.out.println(friendgroup + ":" + friendname);
                String[] element = new String[]{friendname, friendgroup};
                list.add(element);
            }


            jsonObject.put("username", UserName);
               for(int i = 0; i < list.size(); i++) {
                JSONObject bean = new JSONObject();
                String Aim = list.get(i)[0], friendgroup = list.get(i)[1];
//                System.out.println(Aim + ":" + friendgroup);
                String Sql = "select * from user where binary username=?";
                statement = connection.prepareStatement(Sql);
                statement.setString(1, Aim);
                ResultSet LittleResultSet = statement.executeQuery();
                while (LittleResultSet.next()) {
                    bean.put("username", Aim);
                    String name = LittleResultSet.getString("name");
                    String password = LittleResultSet.getString("password");
                    String path = LittleResultSet.getString("imagepath");
                    String email=  LittleResultSet.getString("email");
                    bean.put("name", name);
                    bean.put("imagepath", path);
                    bean.put("password", password);
                    bean.put("email", email);
                    bean.put("friendgroup", friendgroup);
                    jsonArray.add(bean);

                }
                this.CloseResultSet(LittleResultSet);
            }
            this.CloseResultSet(resultSet);
            this.CloseStatement(statement);
            this.CloseConnection(connection);
            jsonObject.put("result", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public List<String> GetGroupMember(String GroupName, String UserName) {
        List<String> list = new ArrayList<>();
        String sql = "select * from user_friend_table where username=? and friendgroup=?";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            statement.setString(2, GroupName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String bean = resultSet.getString("friendname");
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void UpdateFriend(String column, String value, String friendname) {//更新用户数据
        String sql = "UPDATE user_friend_table SET " + column + "=? WHERE friendname=?";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, value);
            statement.setString(2, friendname);
            statement.executeUpdate();
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void UpdateUser(String column, String value, String username) {//更新用户数据
        String sql = "UPDATE user SET " + column + "=? WHERE username=?";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, value);
            statement.setString(2, username);
            statement.executeUpdate();
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void CreateUser(UserModel User) {//插入用户数据
        String sql;
        sql = "INSERT INTO user (username,password,name,imagepath,email) " + "VALUES (" +  "'" + User.getUserName() + "'" + "," + "'" + User.getPassword() + "'" + "," + "'" + User.getName() + "'" + ","+ "'"  + User.getImgPath() + "'" + ","+ "'" + User.getEmail() + "'" +  ");";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.execute();
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void insert(String UserName, String FriendName, String Message) {//插入数据
//        String sql;
//        if (Message.equals("doesntsay"))
//            sql = "INSERT INTO user_friend_table (username,friendname) " + "VALUES (" +  "'" + UserName + "'" + "," + "'" + FriendName + "'" + ");";
//        else
//            sql = "INSERT INTO user_talk_table (send_user,res_user,message) " + "VALUES (" + "'" + UserName + "'" + "," + "'" + FriendName + "'" + "," + "'" + Message + "'" + ");";
//        try {
//            connection = this.GetConnection();
//            statement = connection.prepareStatement(sql);
//            statement.execute();
//            this.CloseStatement(statement);
//            this.CloseConnection(connection);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public void AddGroup(String UserName, String NewGroupName) {
        String sql = "insert into user_group_table (username,usergroup) values (?,?)";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            statement.setString(2, NewGroupName);
            statement.executeUpdate();
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public JSONObject query(String selection, String UserName, String FriendName) {//查询数据
        String sql;
        String ColumnLabel;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        ResultSet resultSet = null;
        switch (selection) {
            case "friend" : {
                sql = "select * from user_friend_table where username=?";
                ColumnLabel = "friendname";
                break;
            }
            case "message" : {
                sql = "select * from user_talk_table where send_user=? and res_user=?";
                ColumnLabel = "message";
                break;
            }
            default : return null;
        }
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            if(ColumnLabel.equals("message")) {
                statement.setString(2, FriendName);
            }
            resultSet = statement.executeQuery();

            jsonObject.put("username", UserName);
            if (!FriendName.equals("")) jsonObject.put("FriendName", FriendName);
            while (resultSet.next()) {
                JSONObject bean = new JSONObject();
                String res = resultSet.getString(ColumnLabel);
                bean.put(ColumnLabel, res);
                jsonArray.add(bean);
            }
            this.CloseResultSet(resultSet);
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("result", jsonArray);
        return jsonObject;
    }

    public void DeleteGroup(String GroupName, String UserName) {
        String sql = "delete from user_group_table where username=? and usergroup=?";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            statement.setString(2, GroupName);
            statement.executeUpdate();
            this.CloseConnection(connection);
            this.CloseStatement(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONArray GetAllUser() {
        JSONArray list = new JSONArray();
        String sql = "select * from user";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JSONObject bean = new JSONObject();
                String UserName = resultSet.getString("username");
                String Password = resultSet.getString("password");
                String Email = resultSet.getString("email");
                String Name = resultSet.getString("name");
                String ImgPath = resultSet.getString("imagepath");
                bean.put("username", UserName);
                bean.put("password", Password);
                bean.put("email", Email);
                bean.put("name", Name);
                bean.put("imagepath", ImgPath);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public void delete(String UserName, String FriendName) { //删除数据
        String sql = "delete from user_friend_table where username=? and friendname=?";
        try {
            connection = this.GetConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, UserName);
            statement.setString(2, FriendName);
            statement.executeUpdate();
            this.CloseStatement(statement);
            this.CloseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
