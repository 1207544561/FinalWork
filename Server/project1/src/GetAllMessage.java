import Model.RealMessageModel;
import Model.SARModel;
import Model.UserModel;
import com.mysql.cj.xdevapi.JsonArray;
import database.OpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.java2d.pipe.AAShapePipe;

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

@WebServlet("/GetAllMessage")
public class GetAllMessage extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        OpUtil operator = new OpUtil();
        String UserName;
        JSONArray jsonArray = new JSONArray();
        List<SARModel> list = new ArrayList<>();
        List<SARModel> Newlist = new ArrayList<>();
        List<RealMessageModel> RealList = new ArrayList<>();

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
                UserName = AcJson.getString("username");
                list = operator.getallmessage(UserName);
                for(int  i = 0; i < list.size() - 1; i++) { //获取最后一条消息
                    String NowSender = list.get(i).getSender();
                    String NowReceiver = list.get(i).getReceiver();
                    String NextSender = list.get(i + 1).getSender();
                    String NextReceiver = list.get(i + 1).getReceiver();
                    if((NowSender.equals(NextSender) && NowReceiver.equals(NextReceiver)) || (NowSender.equals(NextReceiver) && NowReceiver.equals(NextSender))) {
                        continue;
                    } else {
                        SARModel SAR = new SARModel(list.get(i).getSender(),
                                list.get(i).getReceiver(),
                                list.get(i).getMessage());
                        Newlist.add(SAR);
                    }
                }
                Newlist.add(new SARModel(list.get(list.size() - 1).getSender(),
                        list.get(list.size() - 1).getReceiver(),
                        list.get(list.size() - 1).getMessage()));

                for(int i = 0; i < Newlist.size(); i++) {
                    String Name;
                    if (!Newlist.get(i).getSender().equals(UserName)) {
                        Name = Newlist.get(i).getSender();
                    } else {
                        Name = Newlist.get(i).getReceiver();
                    }
                    UserModel user = operator.GetUser(Name);
                    String Group = operator.GetGroup(UserName, Name);
                    RealMessageModel bean = new RealMessageModel(Name,
                            user.getPassword(),
                            user.getName(),
                            user.getEmail(),
                            user.getImgPath(),
                            Group,
                            Newlist.get(i).getMessage()
                    );
                    RealList.add(bean);
                }

            } else {
                System.out.println("get the json failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reallist", RealList);
//        SenderImg = operator.getUserInformation(SendUser, "imagepath");
//        ReceiverImg = operator.getUserInformation(ReceiveUser, "imagepath");
//        list = operator.GetMessage(SendUser, ReceiveUser, SenderImg, ReceiverImg);

//        jsonObject.put("list", list);
        out.write(jsonObject.toString());
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
