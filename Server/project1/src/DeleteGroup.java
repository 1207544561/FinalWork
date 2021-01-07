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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/DeleteGroup")
public class DeleteGroup extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OpUtil operator = new OpUtil();
        List<String> FriendGroupList = new ArrayList<>();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json");
        String AccaptJson = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer("");
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            bufferedReader.close();
            AccaptJson = stringBuffer.toString();
            System.out.println("the receive json is:" + AccaptJson);
            if (AccaptJson != null) {
                JSONObject AcJson = JSONObject.fromObject(AccaptJson);
                String GroupName = AcJson.getString("groupname");
                String UserName = AcJson.getString("username");
                operator.DeleteGroup(GroupName, UserName);
                FriendGroupList = operator.GetGroupMember(GroupName, UserName);
                for(int i = 0; i < FriendGroupList.size(); i++) {
                    operator.UpdateFriend("friendgroup", "我的好友", FriendGroupList.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
