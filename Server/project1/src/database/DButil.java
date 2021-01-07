package database;


import javax.jws.soap.SOAPBinding;
import java.sql.*;

public class DButil {
    private String Url="jdbc:mysql://localhost:3306/chatdb?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private String UserName = "root", PassWord = "163cn445";
    private String driver = "com.mysql.cj.jdbc.Driver";

    public Connection GetConnection() { //数据库连接
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(Url, UserName, PassWord);
            System.out.println("connect success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }




    //关闭
    public void CloseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void CloseStatement(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void CloseResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
