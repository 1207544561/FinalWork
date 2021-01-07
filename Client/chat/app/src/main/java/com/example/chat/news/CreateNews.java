package com.example.chat.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.DataBase.database;
import com.example.chat.R;
import com.example.chat.ViewShape.BitMapShape;
import com.example.chat.login.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class CreateNews extends AppCompatActivity {
    public final static int Send_Pwd = 1;
    Handler handler;
    Button UpLoad;
    BitMapShape op;
    SharedPreferences sharedPreferences;
    protected void onCreate(Bundle savedInstanceState) {
        String UserName, Name, ImgPath;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnews);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        UserName = sharedPreferences.getString("username", "username");
        Name = sharedPreferences.getString("name", "name");
        ImgPath = sharedPreferences.getString("imgpath", "imgPath");
        UpLoad = (Button) findViewById(R.id.create_news_button);

        ImageView imageView = (ImageView) findViewById(R.id.create_news_icon);
        Bitmap bitmap = BitmapFactory.decodeFile(ImgPath); //Path
        op = new BitMapShape(bitmap);
        Bitmap out = op.getCirleBitmap();
        imageView.setImageBitmap(out);
        TextView textView1 = (TextView) findViewById(R.id.create_news_name);
        textView1.setText(UserName);
        UpLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.create_news_news);
                String News = textView.getText().toString();
                Thread thread = new Thread(new UpLoadThread(UserName, Name, ImgPath, News));
                thread.start();
                finish();
            }
        });
    }

    public HttpClient getHttpClient() { //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    //发送用户名和密码到服务器校验并返回哦用户模型和是否存在用户
    public void upload(String UserName, String News, String Name, String ImgPath) {
        //向Servlet发送数据用于操作数据库
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/UpdateNews";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("username", UserName);
            json.put("name", Name);
            json.put("news", News);
            json.put("imgpath", ImgPath);
//            json = jsonUtil.CreateUserJson(User);

            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            System.out.println("发送成功!");



//            //接收Servlet返回的参数
//            int httpcode = httpResponse.getStatusLine().getStatusCode();
//            if(httpcode == HttpURLConnection.HTTP_OK && httpResponse != null) {
//                HttpEntity httpEntity = httpResponse.getEntity();
//
//                InputStream inputStream = httpEntity.getContent();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                String line;
//                while((line = bufferedReader.readLine()) != null) {
//                    result.append(line);
//                }
//                bufferedReader.close();
//                System.out.println("User:" + result.toString());
//                JSONObject jsonObject = new JSONObject(result.toString());
//                String password = jsonObject.getString("password");
//                Message msg = Message.obtain();
//                msg.what = Send_Pwd;
//                msg.obj = password;
//                handler.sendMessage(msg);
//                JSONObject jsonObject1 = jsonObject.getJSONObject("param");
//                haveUser = jsonObject.getBoolean("haveUser");
//                User = (UserModel) jsonObject.get("UserModel");

//                String name = jsonObject1.getString("name");
//                String username = jsonObject1.getString("username");
//                List<String> list = op.GetParam(jsonObject, "message");
//                Message message = Message.obtain();
//                message.obj = list;
//                message.what = MSG_SUCCESS;
//                handler.sendMessage(message);
//                handler.obtainMessage(MSG_SUCCESS, list);
//                System.out.println(name + "   " + username);
//                System.out.println("返回的值是:" + name);
//                Iterator<String> it = list.iterator();
//                while (it.hasNext()) {
//                    System.out.println((String) it.next());
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class UpLoadThread implements Runnable {
        String UserName, Name, ImgPath, News;
        public UpLoadThread(String UserName, String Name, String ImgPath, String News) {
            this.UserName = UserName;
            this.Name = Name;
            this.ImgPath = ImgPath;
            this.News = News;
        }
        public void run() {
            upload(UserName, News, Name, ImgPath);
        }
    }
}
