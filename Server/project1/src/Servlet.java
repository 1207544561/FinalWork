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
import java.util.ArrayList;
import java.util.List;


import database.OpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import database.DButil;

@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
    private String defaultmessage = "doesntsay";
    String username;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        DButil oprator = new DButil();
//        Connection connection = oprator.GetConnection();
        // super.doGet(request, response);
        // response.getWriter().write("hello servlet!");
        doPost(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //接收Android app传来的消息
        response.setContentType("text/html;charset=utf-8");
        OpUtil operator = new OpUtil();

//        operator.insert("1207544561", "1033243254", "你好啊");
//        operator.insert("1207544561", "1033243254", "我叫江宇航");
//        operator.insert("1207544561", "1033243254", "我是算法大师");
//        operator.insert("1207544561", "1033243254", "我真的是算法大师");
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
                username = (String) AcJson.get("username");
                System.out.println(AcJson.get("username"));
//                PrintWriter out = response.getWriter();
//                out.write(AcJson.get("email").toString());
            } else {
//                PrintWriter out = response.getWriter();
//                out.write("get the json failed!");
                System.out.println("get the json failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //发送数据到android app
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1;
        JSONObject jsonObject2 = new JSONObject();
        List<String> list = operator.GetFriendGroup(username);
        jsonObject.put("group", list);
//        System.out.println(jsonObject.get("group").toString());
        jsonObject1 = operator.GetFriendList(username);
        System.out.println(jsonObject1.toString());
//        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//        jsonObject1.put("name", "123");
//        jsonObject1.put("username", "1207544561");


//        jsonObject1 = operator.query("message", "1207544561", "1033243254");
        jsonObject.put("information", jsonObject1);
        out.write(jsonObject.toString());
        out.flush();
        out.close();
    }

}
