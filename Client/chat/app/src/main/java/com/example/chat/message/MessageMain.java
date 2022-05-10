package com.example.chat.message;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.R;

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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MessageMain extends AppCompatActivity {
    Handler handler;
    private static final int Send_List = 1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_and_recevive_message);
        Intent intent = getIntent();
//        User.getUserName()
//        User.getName()
//        User.getImgPath()
//        User.getEmail()
//        User.getPassword()
//        User.getFriendgroup()
//        result
//        imgpath

        String[] args = intent.getStringArrayExtra("nowuser");
        Thread thread = new Thread(new SendUserThread(args[6], args[0]));
        thread.start();
        TextView sendtitle = (TextView) findViewById(R.id.SendTitle);
        sendtitle.setText(args[1]);
        ImageView Back = (ImageView) findViewById(R.id.MessageBackButton);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                List<MessageModel> list = (List<MessageModel>) msg.obj;
                switch (msg.what) {
                    case Send_List : {
                        ListView listView = (ListView) findViewById(R.id.message_list_view);
                        MessageAdapter adapter = new MessageAdapter(MessageMain.this, list, args[6]);
                        listView.setAdapter(adapter);

                        Button SendMSG = (Button) findViewById(R.id.SRSend);
                        SendMSG.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView textView = (TextView) findViewById(R.id.MessageMultiLine);
                                String MsgText = textView.getText().toString();
                                if(!MsgText.equals("")) {
                                    Thread thread1 = new Thread(new SendMessageThread(args[6], args[0], MsgText));
                                    thread1.start();
                                    list.add(new MessageModel(args[6], MsgText, args[7]));

                                    listView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    listView.invalidate();
                                    textView.getEditableText().clear();
                                }

//                                refresh();

                            }
                        });
                        break;
                    }
                }
            }
        };

    }

    public void refresh() {
        onCreate(null);
    }

    public HttpClient getHttpClient() { //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }


    public void send(String SendUser, String ReceiveUser) {
        //向Servlet发送数据用于操作数据库
        List<MessageModel> list = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105 三号ip:192.168.43.200
        String url = "http://120.79.114.234/project1/GetLastMessage";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("senduser", SendUser);
            json.put("receiveuser", ReceiveUser);
//            json = jsonUtil.CreateUserJson(User);

            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
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
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    MessageModel bean = new MessageModel(
                            jsonObject1.getString("user"),
                            jsonObject1.getString("message"),
                            jsonObject1.getString("imgPath"));
                    list.add(bean);
                }
                Message msg = Message.obtain();
                msg.what = Send_List;
                msg.obj = list;
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


    public void SendMessageMethod(String SendUser, String ReceiveUser, String Message) {
        //向Servlet发送数据用于操作数据库
        List<MessageModel> list = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105 三号ip:192.168.43.200
        String url = "http://120.79.114.234/project1/SaveMessage";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("senduser", SendUser);
            json.put("receiveuser", ReceiveUser);
            json.put("message", Message);
//            json = jsonUtil.CreateUserJson(User);

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


    class SendUserThread implements Runnable {
        String Sender, Recevier;
        public SendUserThread(String Sender, String Recevier) {
            this.Sender = Sender;
            this.Recevier = Recevier;
        }
        public void run() {
            send(Sender, Recevier);
        }
    }

    class SendMessageThread implements Runnable {
        String Sender, Recevier, Message;
        public SendMessageThread(String Sender, String Recevier, String Message) {
            this.Sender = Sender;
            this.Recevier = Recevier;
            this.Message = Message;
        }
        public void run() {
            SendMessageMethod(Sender, Recevier, Message);
        }
    }


}
