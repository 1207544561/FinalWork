import database.OpUtil;
import net.sf.json.JSONArray;
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
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/UserFriendList")
public class UserFriendList extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OpUtil operator = new OpUtil();
        JSONArray jsonArray = new JSONArray();
        JSONObject FriendObject;
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
            System.out.println("the receive json is:sagfagag" + AccaptJson);
            if (AccaptJson != null) {
                JSONObject AcJson = JSONObject.fromObject(AccaptJson);
                String UserName = AcJson.getString("username");
                JSONArray UserList = operator.GetAllUser();
                List<String> FriendList = operator.QueryFriendList(UserName);
                for(int i = 0; i < UserList.size(); i++) {
                    boolean judge = false;
                    JSONObject bean_first = UserList.getJSONObject(i);
                    String U = bean_first.getString("username");
                    if(U.equals(UserName)) continue;
                    for(int j = 0; j < FriendList.size(); j++) {
                        String F = FriendList.get(j);
                        if(U.equals(F)) judge = true;
                    }
                    JSONObject bean = new JSONObject();
                    bean.put("username", bean_first.get("username").toString());
                    bean.put("password", bean_first.get("password").toString());
                    bean.put("name", bean_first.get("name").toString());
                    bean.put("email", bean_first.get("email").toString());
                    bean.put("imagepath", bean_first.get("imagepath").toString());
                    if(judge) bean.put("friendgroup", operator.GetGroup(UserName, bean_first.getString("username").toString()));
                    else bean.put("friendgroup", "");
                    bean.put("isFriend", judge);
                    jsonArray.add(bean);
                }
                PrintWriter out = response.getWriter();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("friendlist", jsonArray);
                System.out.println(jsonObject.toString());
                out.write(jsonObject.toString());
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
