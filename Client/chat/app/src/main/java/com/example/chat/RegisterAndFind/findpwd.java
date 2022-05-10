package com.example.chat.RegisterAndFind;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.chat.DataBase.database;
import com.example.chat.R;
import com.example.chat.login.MainActivity;
import com.example.chat.login.UserModel;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.PhantomReference;
import java.net.HttpURLConnection;

public class findpwd extends AppCompatActivity {
    public final static int Send_Pwd = 1;
    Handler handler;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find);
        final database dbOperator = new database(this);
        Button Send = (Button)findViewById(R.id.send), back = (Button)findViewById(R.id.Fback);
        // 封印!!!!!!(以发送短信的方式找回密码)
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Phone = (EditText)findViewById(R.id.phone), UserName = (EditText)findViewById(R.id.username);
                String phone = Phone.getText().toString();
                String UName = UserName.getText().toString();
//                String password = dbOperator.query(UName, "password");
                handler = new Handler() {
                    @SuppressLint("HandlerLeak")
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what) {
                            case Send_Pwd : {
                                String password = (String) msg.obj;
                                String message = "您的密码是" + password + ",请牢记您的密码.";

                                try {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(phone, null, message, null, null);
                                    Toast.makeText(getApplicationContext(), "已发送!",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(findpwd.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),"未发送成功,请稍后重试!",Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                                break;
                            }
                            default : {
                                throw new IllegalStateException("Unexpected value: " + msg.what);
                            }
                        }

                    }
                };

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(findpwd.this, MainActivity.class);
                startActivity(intent);
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
    public void send(String UserName) {
        //向Servlet发送数据用于操作数据库
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://120.79.114.234/project1/FindPwd";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("username", UserName);
//            json = jsonUtil.CreateUserJson(User);

            StringEntity stringEntity = new StringEntity(json.toString());
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            System.out.println("发送成功!");



            //接收Servlet返回的参数
            int httpcode = httpResponse.getStatusLine().getStatusCode();
            if(httpcode == HttpURLConnection.HTTP_OK && httpResponse != null) {
                HttpEntity httpEntity = httpResponse.getEntity();

                InputStream inputStream = httpEntity.getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;
                while((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                bufferedReader.close();
                System.out.println("User:" + result.toString());
                JSONObject jsonObject = new JSONObject(result.toString());
                String password = jsonObject.getString("password");
                Message msg = Message.obtain();
                msg.what = Send_Pwd;
                msg.obj = password;
                handler.sendMessage(msg);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class GetPwdThread implements Runnable {
        String UserName, Password;
        public GetPwdThread(String UserName) {
            this.UserName = UserName;
        }
        public void run() {
            send(UserName);
        }
    }
}

