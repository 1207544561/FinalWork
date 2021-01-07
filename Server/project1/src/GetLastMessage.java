import Model.MessgeModel;
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

@WebServlet("/GetLastMessage")
public class GetLastMessage extends HttpServlet {
    String SendUser, ReceiveUser, SenderImg, ReceiverImg;
    JSONArray list;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                SendUser = AcJson.get("senduser").toString();
                ReceiveUser = AcJson.get("receiveuser").toString();
//                System.out.println(AcJson.get("username"));
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

        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();
        SenderImg = operator.getUserInformation(SendUser, "imagepath");
        ReceiverImg = operator.getUserInformation(ReceiveUser, "imagepath");
        list = operator.GetMessage(SendUser, ReceiveUser, SenderImg, ReceiverImg);
//        System.out.println(jsonObject.get("group").toString
//        System.out.println(jsonObject1.toString());
//        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//        jsonObject1.put("name", "123");
//        jsonObject1.put("username", "1207544561");


//        jsonObject1 = operator.query("message", "1207544561", "1033243254");
        jsonObject.put("list", list);
        out.write(jsonObject.toString());
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
