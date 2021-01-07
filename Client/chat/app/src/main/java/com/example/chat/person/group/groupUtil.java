package com.example.chat.person.group;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.chat.JsonUtils.JsonUtil;
import com.example.chat.R;
import com.example.chat.person.Information;
import com.example.chat.person.ui.home.HomeFragment;
import com.example.chat.person.ui.home.ModifyDialog;

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
import java.util.List;
import java.util.Map;

public class groupUtil extends AppCompatActivity {
    Handler handler;
    JsonUtil op = new JsonUtil();
    private static final int Send_Group = 1;
    String result;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_group);
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        result = sharedPreferences.getString("username", "cantsearch");
        Thread thread = new Thread(new GroupThread(result));
        thread.start();
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                List<String> list = (List<String>) msg.obj;
                switch (msg.what) {
                    case Send_Group : {
                        ListView listView = findViewById(R.id.group_list);
                        GroupAdapter adapter = new GroupAdapter(groupUtil.this, list, result);
                        listView.setAdapter(adapter);
                        Button button = (Button) findViewById(R.id.quit_button);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(groupUtil.this, Information.class);
                                startActivity(intent);
                                finish();
                            }
                        });

//                        Button button1 = (Button) findViewById(R.id.create_group_button);
//                        button1.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                showDialog();
//                            }
//                        });
                    }
                }
            }
        };


    }

//        public void alertAddDialog(Context context, String title, int currentGroup){
//        final int group = currentGroup;
//
//        dialog = new ModifyDialog(context, title, null);
//        edit_modify = dialog.getEditText();
//        dialog.setOnClickCommitListener(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HomeFragment.addChild(group, edit_modify.getText().toString());
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

    private void showDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            editText.setBackground(getResources().getDrawable(R.drawable.shape_textview));
//        builder.setTitle("新建分组");
        new AlertDialog.Builder(this).setTitle("新建分组")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String group = editText.getText().toString();
                        Thread thread = new Thread(new AddThread(result, group));
                        thread.start();

                        //按下确定键后的事件
//                        Toast.makeText(getApplicationContext(), editText.getText().toString(),Toast.LENGTH_LONG).show();

                    }
                }).setNegativeButton("取消",null).show();

    }


    public HttpClient getHttpClient() { //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    public void add(String UserName, String NewGroupName) {
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/CreateGroup";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("username", UserName);
            json.put("newgroup", NewGroupName);
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

    public void send(String UserName) {
        //向Servlet发送数据用于操作数据库
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/GetGroup";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("username", UserName);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
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
                System.out.println(result.toString());
                JSONObject jsonObject = new JSONObject(result.toString());
                List<String> list = op.GetGroupList(jsonObject);
//                JSONObject jsonObject1 = jsonObject.getJSONObject("param");
//                String name = jsonObject1.getString("name");
//                String username = jsonObject1.getString("username");
//                Map<String, Object> map = op.GetPerson(jsonObject);
                Message message = Message.obtain();
                message.obj = list;
                message.what = Send_Group;
                handler.sendMessage(message);
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

    class GroupThread implements Runnable {
        String UserName;
        public GroupThread(String UserName) {
            this.UserName = UserName;
        }
        public void run() {
            send(UserName);
        }
    }

    class AddThread implements Runnable {
        String UserName, NewGroupName;
        public AddThread(String UserName, String NewGroupName) {
            this.UserName = UserName;
            this.NewGroupName = NewGroupName;
        }

        public void run() {
            add(UserName, NewGroupName);
        }
    }

}
