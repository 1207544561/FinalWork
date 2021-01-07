import Model.NewsModel;
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/GetNews")
public class GetNews extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserModel user;
        String ImgPath;
        List<NewsModel> list;
        boolean Judge = true;
        OpUtil operator = new OpUtil();
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
            System.out.println("the receive json is:News" + AccaptJson);
            if (AccaptJson != null) {
                JSONObject AcJson = JSONObject.fromObject(AccaptJson);
                list = operator.getNews();
                PrintWriter out = response.getWriter();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("newslist1", list);
                out.write(jsonObject.toString());
                System.out.println(jsonObject.toString());
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
