import Model.UserModel;
import database.OpUtil;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet("/CreateUser")
public class CreateUser extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OpUtil operator = new OpUtil();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json");
        String AccaptJson = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream(), "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer("");
            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            bufferedReader.close();
            AccaptJson = stringBuffer.toString();
            System.out.println("the receive json is:" + AccaptJson);
            if(AccaptJson != null) {
                JSONObject AcJson = JSONObject.fromObject(AccaptJson);
                String UserName = AcJson.get("username").toString();
                UserModel User = new UserModel(AcJson.get("username").toString(),
                        AcJson.get("password").toString(),
                        AcJson.get("name").toString(),
                        AcJson.get("email").toString(),
                        AcJson.get("imagepath").toString());
                operator.CreateUser(User);
                operator.AddGroup(UserName, "我的好友");
                operator.AddGroup(UserName, "特别关心");
            } else {
                System.out.println("get the json failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
