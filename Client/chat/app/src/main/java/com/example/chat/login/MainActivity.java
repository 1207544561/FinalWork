package com.example.chat.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.chat.JsonUtils.JsonUtil;
import com.example.chat.RegisterAndFind.register;
import com.example.chat.RegisterAndFind.findpwd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.DataBase.database;
import com.example.chat.person.Information;
import com.example.chat.ViewShape.BitMapShape;

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
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ImageView UserPhoto;
    TextView Register, FindPwd;
    Button Login;
    database dbOperator;
    SharedPreferences sharedPreferences;
    BitMapShape op;
    boolean haveUser, isHaveUser;
    String ImagePath, Pwd; //
    UserModel User;
    JsonUtil jsonUtil = new JsonUtil();
    Handler handler;
    public static final int Send_User = 2, Send_Img = 1, MSG_FAILED = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //创建数据库对象
        dbOperator = new database(this);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String result = sharedPreferences.getString("username", "cantsearch");
        if(!result.equals("cantsearch")) {
            Intent intent = new Intent(MainActivity.this, Information.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        requestWritePermission();
        //获取登录按钮
        Login = (Button)findViewById(R.id.login);

        //创建数据库对象
        dbOperator = new database(this);
        //获取注册和找回密码
        Register = (TextView)findViewById(R.id.register);
        FindPwd = (TextView)findViewById(R.id.findpwd);
        EditText Password = (EditText)findViewById(R.id.Input_PassWord);

        Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText UserName = (EditText)findViewById(R.id.Input_UserName);
                String username = UserName.getText().toString();
                Thread thread = new Thread(new GetImgThread(username));
                thread.start();

                try {
                    TimeUnit.MILLISECONDS.sleep(5 * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //获取子线程传递的消息
                handler = new Handler() {
                    @SuppressLint("HandlerLeak")
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        switch (msg.what) {
                            case Send_Img : {
                                try {
                                    isHaveUser = jsonObject.getBoolean("param");
                                    if(isHaveUser) {
                                        ImagePath = jsonObject.getString("imgpath");
                                    }

                                    if(isHaveUser) {
                                        UserPhoto = (ImageView)findViewById(R.id.UserPhoto);
                                        Bitmap bitmap = BitmapFactory.decodeFile(ImagePath); //Path
                                        op = new BitMapShape(bitmap);
                                        Bitmap out = op.getCirleBitmap();
                                        UserPhoto.setImageBitmap(out);
                                    } else {
                                        Toast.makeText(MainActivity.this, "用户名错误或用户不存在,请检查用户名是否正确.", Toast.LENGTH_SHORT);
                                    }
                                    break;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            default : {
                                throw new IllegalStateException("Unexpected value: " + msg.what);
                            }
                        }
                    }
                };
//                try {
//                    TimeUnit.MILLISECONDS.sleep(5 * 100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                String Path = dbOperator.query(username, "imagepath");


            }
        });

        //数据库模型
        // db = databaseHelper.getReadableDatabase();
        // UserModel user = new UserModel("1207544561", "163cn445", "asd", "1207544561@qq.com");
        // dbOperator.insert(user);

        //登录
        Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取用户名和密码
                    EditText UserName = (EditText)findViewById(R.id.Input_UserName), Password = (EditText)findViewById(R.id.Input_PassWord);
                    String username = UserName.getText().toString(), password = Password.getText().toString();
                    Thread thread = new Thread(new SendUserThread(username, password));
                    thread.start();
                    //获取子线程传递的消息
                    handler = new Handler() {
                        @SuppressLint("HandlerLeak")
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what) {
                                case Send_User : {
                                    JSONObject jsonObject = (JSONObject) msg.obj;
                                    try {
                                        haveUser = jsonObject.getBoolean("haveUser");
                                        System.out.println("judge:" + haveUser);
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("UserModel");
                                        User = new UserModel(jsonObject1.getString("userName"),
                                                jsonObject1.getString("password"),
                                                jsonObject1.getString("name"),
                                                jsonObject1.getString("email"),
                                                jsonObject1.getString("imgPath")
                                                );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if(haveUser) {
                                        String user = User.getName();
                                        Toast.makeText(MainActivity.this, "欢迎" + user, Toast.LENGTH_LONG).show();
                                        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("username", username);
//                        editor.putString("password", dbOperator.query(username, "password"));
//                        editor.putString("name", user);
//                        editor.putString("email", dbOperator.query(username, "email"));

                                        editor.putString("username", User.getUserName());
                                        editor.putString("password", User.getPassword());
                                        editor.putString("name", User.getName());
                                        editor.putString("email", User.getEmail());
                                        editor.putString("imgpath", User.getImgPath());
                                        editor.commit();
                                        Intent intent = new Intent(MainActivity.this, Information.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "用户名或密码错误,请重试", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                                default : {
                                    throw new IllegalStateException("Unexpected value: " + msg.what);
                                }
                            }
                        }
                    };
//                    try {
//                        TimeUnit.MILLISECONDS.sleep(5 * 100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    String pwd = dbOperator.query(username, "password");
//                    Toast.makeText(MainActivity.this, "1111111111" + pwd, Toast.LENGTH_LONG);
                    // if(dbOperator.querypwd(username, password)) {
//                    if(haveUser) {
//                        String user = dbOperator.query(username, "name");
//                        Toast.makeText(MainActivity.this, "欢迎" + user, Toast.LENGTH_LONG).show();
//                        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
////                        editor.putString("username", username);
////                        editor.putString("password", dbOperator.query(username, "password"));
////                        editor.putString("name", user);
////                        editor.putString("email", dbOperator.query(username, "email"));
//
//                        editor.putString("username", User.getUserName());
//                        editor.putString("password", User.getPassword());
//                        editor.putString("name", User.getName());
//                        editor.putString("email", User.getEmail());
//                        editor.putString("imgpath", User.getImgPath());
//                        editor.commit();
//                        Intent intent = new Intent(MainActivity.this, Information.class);
//                        startActivity(intent);
//                        finish();
//
//                    }
//                    else {
//                        Toast.makeText(MainActivity.this, "用户名或密码错误,请重试", Toast.LENGTH_SHORT).show();
//                    }
                }
        });

        //注册
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, register.class);
                startActivity(intent);
                finish();
            }
        });

        //找回密码
        FindPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, findpwd.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //权限方法
    private void requestWritePermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    public HttpClient getHttpClient() { //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    //发送用户名到服务器用于查询用户头像
    public void GetImg(String UserName) {
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/GetImg";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("username", UserName);
            StringEntity stringEntity = new StringEntity(json.toString());
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            System.out.println("发送成功!");

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
                System.out.println("Img:" + result.toString());
                JSONObject jsonObject = new JSONObject(result.toString());
//                isHaveUser = jsonObject.getBoolean("param");
//                if(isHaveUser) {
//                    ImagePath = jsonObject.getString("imgpath");
//                }
                Message msg = Message.obtain();
                msg.what = Send_Img;
                msg.obj = jsonObject;
                handler.sendMessage(msg);
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

    //发送用户名和密码到服务器校验并返回哦用户模型和是否存在用户
    public void send(String UserName, String Password) {
        //向Servlet发送数据用于操作数据库
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105 三号ip:192.168.43.200
        String url = "http://192.168.0.114:8080/project1/JudgeUser";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("username", UserName);
            json.put("password", Password);
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
                Message msg = Message.obtain();
                msg.what = Send_User;
                msg.obj = jsonObject;
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

    class SendUserThread implements Runnable {
        String UserName, Password;
        public SendUserThread(String UserName, String Password) {
            this.UserName = UserName;
            this.Password = Password;
        }
        public void run() {
            send(UserName, Password);
        }
    }

    class GetImgThread implements Runnable {
        String UserName;
        public GetImgThread(String UserName) {
            this.UserName = UserName;
        }
        @Override
        public void run() {
            GetImg(UserName);
        }
    }

}
