package com.example.chat.RegisterAndFind;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.chat.JsonUtils.JsonUtil;
import com.example.chat.R;
import com.example.chat.ViewShape.BitMapShape;
import com.example.chat.login.MainActivity;
import com.example.chat.login.UserModel;
import com.example.chat.DataBase.database;
import android.provider.MediaStore;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class register extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int Send_User_List = 2;
    Handler handler;
    ImageView rimage;
    String picturePath;
    BitMapShape op;
    database dbOperator = new database(register.this);
    SharedPreferences sharedPreferences;
    private List<String> UserList = new ArrayList<>();

    JsonUtil jsonUtil = new JsonUtil();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        requestWritePermission();
        Thread thread = new Thread(new GetTotalUserListThread());
        thread.start();
//        final database dbOperator = new database(this);
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                List<UserModel> TotalUserList = new ArrayList<>();
                List<String> UserNameList = new ArrayList<>();
                TotalUserList = (List<UserModel>) msg.obj;
                for(int i = 0; i< TotalUserList.size(); i++) {
                    UserNameList.add(TotalUserList.get(i).getUserName());
                }

                    switch (msg.what) {
                        case Send_User_List : {
                            final Button regist =  (Button)findViewById(R.id.Rregister), reset = (Button)findViewById(R.id.Rreset), back = (Button)findViewById(R.id.Lback);
                            rimage = (ImageView)findViewById(R.id.RegisterImage);
                            reset.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText Number = (EditText)findViewById(R.id.number), Email = (EditText)findViewById(R.id.email), Pwd = (EditText)findViewById(R.id.pwd),
                                            Rpwd = (EditText)findViewById(R.id.rpwd);
                                    Number.setText(""); Email.setText(""); Pwd.setText(""); Rpwd.setText("");

                                }
                            });

                            regist.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
                                    EditText Number = (EditText)findViewById(R.id.number), Email = (EditText)findViewById(R.id.email), Pwd = (EditText)findViewById(R.id.pwd),
                                            Rpwd = (EditText)findViewById(R.id.rpwd);
                                    String NumberText = Number.getText().toString(), EmailText = Email.getText().toString(), PwdText = Pwd.getText().toString(),
                                            RpwdText = Rpwd.getText().toString();
//                if(dbOperator.getCount(NumberText) > 0) {
                                    if(UserNameList.contains(NumberText)) {
                                        Toast.makeText(register.this, "用户已存在,请登录", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(register.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if(!Pattern.matches(REGEX_EMAIL, EmailText)) { //邮箱验证
                                        Toast.makeText(register.this, "邮箱格式不正确,请重新输入", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(!PwdText.equals(RpwdText)) { //密码一致性验证
                                        Toast.makeText(register.this, "两次输入密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        UserModel user = new UserModel(NumberText, PwdText, "xxxxx", EmailText, picturePath);
                                        dbOperator.insert(user);
                                        Thread thread = new Thread(new SendUserThread(user));
                                        thread.start();
                                        Intent intent = new Intent(register.this, setUserName.class);
                                        Bundle bundle = new Bundle();

                                        //传递username参数
                                        bundle.putString( "username", NumberText);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });

                            rimage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                                }
                            });

                            back.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(register.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            //获取返回的数据，这里是android自定义的Uri地址
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //获取选择照片的数据视图
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            //从数据视图中获取已选择图片的路径
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            //将图片显示到界面上
            // Toast.makeText(register.this, picturePath, Toast.LENGTH_LONG).show();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            op = new BitMapShape(bitmap);
            Bitmap out = op.getCirleBitmap();
            rimage.setImageBitmap(out);
            // rimage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }

    }


    private void requestWritePermission() {
        if (ActivityCompat.checkSelfPermission(register.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(register.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    public HttpClient getHttpClient() { //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    public void send(UserModel User) {
        //向Servlet发送数据用于操作数据库
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/CreateUser";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json = jsonUtil.CreateUserJson(User);
            StringEntity stringEntity = new StringEntity(json.toString());
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            System.out.println("发送成功!");


            //接收Servlet返回的参数
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
//                System.out.println(result.toString());
//                JSONObject jsonObject = new JSONObject(result.toString());
//                JSONObject jsonObject1 = jsonObject.getJSONObject("param");
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

    public void Receive() {
        //向Servlet发送数据用于操作数据库
        List<UserModel> TotalUserList = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/GetUserList";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("message", "GetUserList");
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
                System.out.println(result.toString());
                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject bean = (JSONObject) jsonArray.get(i);
                    UserModel user = new UserModel(bean.getString("username"),
                            bean.getString("password"),
                            bean.getString("name"),
                            bean.getString("email"),
                            bean.getString("imagepath"));
                    TotalUserList.add(user);
//                    UserNameList.add(bean.getUserName());
                }

                sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("username", username);
//                        editor.putString("password", dbOperator.query(username, "password"));
//                        editor.putString("name", user);
//                        editor.putString("email", dbOperator.query(username, "email"));


                editor.commit();

                Message msg = Message.obtain();
                msg.what = Send_User_List;
                msg.obj = TotalUserList;
                handler.sendMessage(msg);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SendUserThread implements Runnable {
        UserModel User;
        public SendUserThread(UserModel User) {
            this.User = User;
        }
        public void run() {
            send(User);
        }
    }

    class GetTotalUserListThread implements Runnable {
        @Override
        public void run() {
            Receive();
        }
    }
}

