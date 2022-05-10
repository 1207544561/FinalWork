package com.example.chat.person.addFriend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.JsonUtils.JsonUtil;
import com.example.chat.R;
import com.example.chat.ViewShape.BitMapShape;
import com.example.chat.person.PersonOwnInformation;

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
import java.util.List;

public class AddFriend extends AppCompatActivity {
    private final static int Send_User = 13;
    ImageView BackButton;
    ImageButton Searchbutton;
    List<UserListModel> UserList, SearchResult;

    ListView listView;
    Handler handler;
    ImageView UserImg;
    Intent intent;
    String Username;
    BitMapShape op;
    JsonUtil jsonUtil = new JsonUtil();
    SharedPreferences sharedPreferences;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String UserName = sharedPreferences.getString("username", "cantsearch");
        String UserPath = sharedPreferences.getString("imgpath", "default");
        listView = (ListView) findViewById(R.id.search_page_list_view);
        Thread thread = new Thread(new GetDataThread(UserName));
        thread.start();
        BackButton = (ImageView) findViewById(R.id.Search_Back_Button);
        BackButton.setOnClickListener(new View.OnClickListener() {
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

                switch (msg.what) {
                    case Send_User : {
                        UserList = (List<UserListModel>) msg.obj;

//                        AllUserAdapter adapter = new AllUserAdapter(AddFriend.this, UserList, UserName);
//                        listView.setAdapter(adapter);
                        Searchbutton = (ImageButton) findViewById(R.id.Search_Page_Button123);
                        Searchbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SearchResult = new ArrayList<>();
//                                System.out.println("111111111111111111111111111111");
//                                Toast.makeText(AddFriend.this, "监听启动", Toast.LENGTH_SHORT).show();
                                TextView textView = (TextView) findViewById(R.id.Search_Text);
                                String Label = textView.getText().toString();
//                                System.out.println("111111111" + Label);
                                for(int i = 0; i < UserList.size(); i++) {
                                    UserListModel user = UserList.get(i);
//                                    System.out.println("322222");
                                    String name = user.getName();
                                    String username = user.getUserName();
                                    if(name.indexOf(Label) != -1 || username.indexOf(Label) != -1) SearchResult.add(user);
                                }

//                                System.out.println(SearchResult.get(0).getName());

                                if(SearchResult.size() == 0) {
                                    Toast.makeText(AddFriend.this, "没有找到要搜索的联系人", Toast.LENGTH_SHORT).show();
                                }
                                else {

                                    AllUserAdapter adapter = new AllUserAdapter(AddFriend.this, SearchResult, UserName);
                                    listView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    listView.invalidate();
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(AddFriend.this, PersonOwnInformation.class);
                                            String Judge = "false";
                                            if(SearchResult.get(position).isFriend()) Judge = "true";
                                            String[] UserString = new String[] {
                                                SearchResult.get(position).getUserName(),
                                                    SearchResult.get(position).getName(),
                                                    SearchResult.get(position).getImgPath(),
                                                    SearchResult.get(position).getEmail(),
                                                    SearchResult.get(position).getPassword(),
                                                    SearchResult.get(position).getUserGroup(),
                                                    UserName, UserPath, Judge};
                                            intent.putExtra("realuser", UserString);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });



                        break;
                    }
                }

            }

        };

//        AllUserAdapter adapter = new AllUserAdapter(AddFriend.this, UserList, UserName);
//        listView.setAdapter(adapter);

//        Searchbutton = (ImageButton) findViewById(R.id.Search_Page_Button123);
//        Searchbutton.setOnClickListener((View.OnClickListener) v -> {
////            System.out.println("111111111111111111111111111111");
//            Toast.makeText(AddFriend.this, "监听启动", Toast.LENGTH_SHORT).show();
//            TextView textView = (TextView) findViewById(R.id.Search_Text);
//            String Label = textView.getText().toString();
////            System.out.println("111111111" + Label);
//            for(int i = 0; i < UserList.size(); i++) {
//                String AIM = UserList.get(i).getName();
//                if(AIM.contains(Label)) SearchResult.add(UserList.get(i));
//            }
//
//            if(SearchResult.size() == 0) {
//                Toast.makeText(AddFriend.this, "没有找到要搜索的联系人", Toast.LENGTH_SHORT).show();
//            }
//            else {
//
//                AllUserAdapter adapter1 = new AllUserAdapter(AddFriend.this, SearchResult, UserName);
//                listView.setAdapter(adapter1);
////                listView.invalidate();
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Intent intent = new Intent(AddFriend.this, PersonOwnInformation.class);
//                        String Judge = "false";
//                        if(SearchResult.get(position).isFriend()) Judge = "true";
//                        String[] UserString = new String[] {
//                                SearchResult.get(position).getUserName(),
//                                SearchResult.get(position).getName(),
//                                SearchResult.get(position).getImgPath(),
//                                SearchResult.get(position).getEmail(),
//                                SearchResult.get(position).getPassword(),
//                                SearchResult.get(position).getUserGroup(),
//                                UserName, UserPath, Judge};
//                        intent.putExtra("realuser", UserString);
//                        startActivity(intent);
//                    }
//                });
//            }
//        });


    }


    public HttpClient getHttpClient() { //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    public void send(String userName) {
        List<UserListModel> list = new ArrayList<>();
//        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://120.79.114.234/project1/UserFriendList";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();

            json.put("username", userName);

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
                System.out.println("我收到了");
                System.out.println(result.toString());
                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("friendlist");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject bean = (JSONObject) jsonArray.get(i);
                    String UserName = bean.getString("username");
                    String Password = bean.getString("password");
                    String Email = bean.getString("email");
                    String Name = bean.getString("name");
                    String ImgPath = bean.getString("imagepath");
                    boolean isfriend = bean.getBoolean("isFriend");
                    String Group = bean.getString("friendgroup");
                    UserListModel user = new UserListModel(UserName, Password, Name, Email, ImgPath, isfriend, Group);
                    list.add(user);

                }
//                System.out.println(list.get(0).getUserName());
                Message msg = Message.obtain();
                msg.what = Send_User;
                msg.obj = list;
                handler.sendMessage(msg);
//                JSONObject jsonObject1 = jsonObject.getJSONObject("param");
//                String name = jsonObject1.getString("name");
//                String username = jsonObject1.getString("username");
//                Map<String, Object> map = op.GetPerson(jsonObject);
//                Message message = Message.obtain();
//                message.obj = map;
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

    class GetDataThread implements Runnable {
        String UserName;
        public GetDataThread(String UserName) {
            this.UserName = UserName;
        }
        public void run() {
            send(UserName);
        }
    }

}
