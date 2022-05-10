package com.example.chat.person;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.JsonUtils.JsonUtil;
import com.example.chat.OwnNews.OwnNewsActivity;
import com.example.chat.R;
import com.example.chat.ViewShape.BitMapShape;
import com.example.chat.message.MessageMain;
import com.example.chat.person.addFriend.AddFriend;
import com.example.chat.person.group.GroupAdapter;
import com.example.chat.person.ui.home.FriendListUser;

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
import java.util.List;

public class PersonOwnInformation extends AppCompatActivity {
    TextView username, name, email, page, group, grouplabel;
    BitMapShape op;
    String text;
    Handler handler;
    public static final int Send_List = 100;
    JsonUtil operator = new JsonUtil();

    Button addorphone, send, beFriend;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_own_information);


        Intent intent = getIntent();
//        User.getUserName()
//        User.getName()
//        User.getImgPath()
//        User.getEmail()
//        User.getPassword()
//        User.getFriendgroup()
//        result
//        userpath
//        judge

        String[] args = intent.getStringArrayExtra("realuser");
        ImageView UserImage = (ImageView) findViewById(R.id.OwnImage);
        Bitmap bitmap = BitmapFactory.decodeFile(args[2]); //Path
        op = new BitMapShape(bitmap);
        Bitmap out = op.getCirleBitmap();
        UserImage.setImageBitmap(out);
        username = (TextView) findViewById(R.id.OwnUserName); name = (TextView) findViewById(R.id.OwnName);
        email = (TextView) findViewById(R.id.OwnEmail); page = (TextView) findViewById(R.id.OwnPage);
        group = (TextView) findViewById(R.id.OwnGroup);
        username.setText(args[0]); name.setText(args[1]); email.setText(args[3]); group.setText(args[5]);

        page.setText(args[1] + "的动态");
        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PersonOwnInformation.this, OwnNewsActivity.class);
                intent1.putExtra("args", args);
                startActivity(intent1);
            }
        });
        send = (Button) findViewById(R.id.SendMessage);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PersonOwnInformation.this, MessageMain.class);
                intent1.putExtra("nowuser", args);
                startActivity(intent1);
                finish();
            }
        });
        beFriend = (Button) findViewById(R.id.BeFriend);
        if(args[8].equals("false")) {
            beFriend.setText("加 好 友");
            beFriend.setOnClickListener(new View.OnClickListener() { //添加好友监听
                @Override
                public void onClick(View v) {
                    Thread thread = new Thread(new AddNewFriendThread(args[6], args[0]));
                    thread.start();
                    Toast.makeText(PersonOwnInformation.this, "已添加好友" + args[1], Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            beFriend.setOnClickListener(new View.OnClickListener() { //其他监听
                @Override
                public void onClick(View v) {
                    Toast.makeText(PersonOwnInformation.this, "该功能暂未开放", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Thread thread = new Thread(new GetGroupListThread(args[6]));
        thread.start();
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                List<String> list = (List<String>) msg.obj;
                switch (msg.what) {
                    case Send_List : {
                        grouplabel = (TextView) findViewById(R.id.GroupLabel);
                        grouplabel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog(list, args);
                            }
                        });
                        break;
                    }
                }
            }
        };


//        beFriend.setText();

    }


    private void showDialog(List<String> list, String[] args) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final EditText editText = new EditText(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            editText.setBackground(this.getResources().getDrawable(R.drawable.shape_textview));
//        builder.setTitle("新建分组");

        new AlertDialog.Builder(this).setTitle("更改分组")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = editText.getText().toString();
                        if(list.contains(text)) {
                            Thread thread = new Thread(new ChangeNewGroupThread(args[6], args[0], text));
                            thread.start();
                            Toast.makeText(PersonOwnInformation.this, "更改完成", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PersonOwnInformation.this, "没有该分组,请创建或添加分组", Toast.LENGTH_SHORT).show();
                        }
                        //按下确定键后的事件
//                        Toast.makeText(getApplicationContext(), editText.getText().toString(),Toast.LENGTH_LONG).show();

                    }
                }).setNegativeButton("取消",null).create().show();
    }


    public HttpClient getHttpClient() { //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    public void AddNewFriend(String UserName, String AimName) {
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://120.79.114.234/project1/AddFriend";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("username", UserName);
            json.put("friendname", AimName);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            System.out.println("发送成功!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void ChangeGroup(String UserName, String FriendName, String NewGroup) {
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://120.79.114.234/project1/ChangeGroup";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("username", UserName);
            json.put("friendname", FriendName);
            json.put("newgroup", NewGroup);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            System.out.println("发送成功!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void GetGroupList(String UserName) {
        //向Servlet发送数据用于操作数据库
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105 三号ip:192.168.43.200
        String url = "http://120.79.114.234/project1/GetGroup";
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
                List<String> list = operator.GetGroupList(jsonObject);
                Message msg = Message.obtain();
                msg.what = Send_List;
                msg.obj = list;
                handler.sendMessage(msg);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class AddNewFriendThread implements Runnable {
        String UserName, AimName;
        public AddNewFriendThread(String UserName, String AimName) {
            this.UserName = UserName;
            this.AimName = AimName;
        }
        @Override
        public void run() {
            AddNewFriend(UserName, AimName);
        }
    }

    class ChangeNewGroupThread implements Runnable {
        String UserName, FriendName, NewGroup;
        public ChangeNewGroupThread(String UserName, String FriendName, String NewGroup) {
            this.UserName = UserName;
            this.NewGroup = NewGroup;
            this.FriendName = FriendName;
        }
        @Override
        public void run() {
            ChangeGroup(UserName, FriendName, NewGroup);
        }
    }

    class GetGroupListThread implements Runnable {
        String UserName;
        public GetGroupListThread(String UserName) {
            this.UserName = UserName;
        }
        @Override
        public void run() {
            GetGroupList(UserName);
        }
    }


}
