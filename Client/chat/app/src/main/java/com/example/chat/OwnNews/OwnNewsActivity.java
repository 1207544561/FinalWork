package com.example.chat.OwnNews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.JsonUtils.JsonUtil;
import com.example.chat.R;
import com.example.chat.ViewShape.BitMapShape;
import com.example.chat.message.MessageMain;
import com.example.chat.person.ui.slideshow.NewsAdapter;
import com.example.chat.person.ui.slideshow.NewsModel;

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

public class OwnNewsActivity extends AppCompatActivity {
    Handler ohandler;
    BitMapShape op;
    private int flag = 0;
    private static final int Send_Own_List = 6;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.own_news_page);


        Intent intent = getIntent();
        String[] args = intent.getStringArrayExtra("args");

        //列表
        ListView listView = (ListView) findViewById(R.id.OwnNewsPage_list_view);
        ohandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                int cas = msg.what;
//                System.out.println(msg.obj.toString());
                switch (cas) {
                    case Send_Own_List : {
                        List<NewsModel> list = (List<NewsModel>) msg.obj;
                        NewsAdapter adapter = new NewsAdapter(OwnNewsActivity.this, list, args[6]);
                        listView.setAdapter(adapter);
                    }
                }


            }
        };

        Thread thread = new Thread(new GetNewsThread(args[0]));
        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        int n = 10000000;
//        while (n != 0) {
//            n--;
//        }

//        try {
//            Thread.sleep(5 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        User.getUserName()
//        User.getName()
//        User.getImgPath()
//        User.getEmail()
//        User.getPassword()
//        User.getFriendgroup()
//        result
//        userpath
//        judge


        //头像
        ImageView imageView = (ImageView) findViewById(R.id.OwnNewsPage_User_Icon);
        Bitmap bitmap = BitmapFactory.decodeFile(args[2]); //Path
        op = new BitMapShape(bitmap);
        Bitmap out = op.getCirleBitmap();
        imageView.setImageBitmap(out);

        //姓名
        TextView textView = (TextView) findViewById(R.id.OwnNewsPage_user_name);
        textView.setText(args[1]);



    }

    public HttpClient getHttpClient() { //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    public void getNowNews(String username) {
//        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        StringBuilder result = new StringBuilder();
        List<NewsModel> list1 = new ArrayList<>();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/GetNowUserOwnNews";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();

            json.put("username", username);

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
                System.out.println(result.toString());
                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("newslist2");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject bean = jsonArray.getJSONObject(i);
                    String News = bean.getString("news");
                    String ImgPath = bean.getString("imgPath");
                    String Name = bean.getString("name");
                    String UserName = bean.getString("userName");
                    System.out.println(News + ImgPath + Name + UserName);
                    NewsModel usernews = new NewsModel(News, UserName, Name, ImgPath);
                    list1.add(usernews);
                }
                Message msg1 = Message.obtain(ohandler);
                msg1.what = Send_Own_List;
                msg1.obj = list1;
                ohandler.sendMessage(msg1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class GetNewsThread implements Runnable {
        private String username;
        public GetNewsThread(String username) {
            this.username = username;
        }
        public void run() {
            getNowNews(username);
        }
    }

}
