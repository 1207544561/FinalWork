package com.example.chat.person.ui.slideshow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.chat.R;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SlideshowFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private final static int Send_List = 1;
    private SlideshowViewModel slideshowViewModel;
    String UserName, ImgPath, Name;
    BitMapShape op;
    private Handler handler;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        UserName = sharedPreferences.getString("username", "default");
        Name = sharedPreferences.getString("name", "default");
        ImgPath = sharedPreferences.getString("imgpath", "default");

        ListView listView = (ListView) root.findViewById(R.id.slide_list_view);
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                List<NewsModel> list = (List<NewsModel>) msg.obj;
                switch (msg.what) {
                    case Send_List : {
                        NewsAdapter adapter = new NewsAdapter(getActivity(), list, UserName);
                        listView.setAdapter(adapter);
                    }
                }
            }
        };
        Thread thread = new Thread(new GetOwnNewsThread(UserName));
        thread.start();


        //头像
        ImageView Icon = (ImageView) root.findViewById(R.id.slide_User_Icon);
        Bitmap bitmap = BitmapFactory.decodeFile(ImgPath);
        op = new BitMapShape(bitmap);
        Bitmap out = op.getCirleBitmap();
        Icon.setImageBitmap(out);
        //名字
        TextView NameView = (TextView) root.findViewById(R.id.slide_user_name);
        NameView.setText(Name);

//        int n = 100000;
//        while(n != 0) {
//            n--;
//        }

//        NewsAdapter adapter = new NewsAdapter()
//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        slideshowViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }



    public HttpClient getHttpClient() { //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    public void get(String username) {
//        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        StringBuilder result = new StringBuilder();
        List<NewsModel> list = new ArrayList<>();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://120.79.114.234/project1/GetNews";
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
                JSONArray jsonArray = jsonObject.getJSONArray("newslist1");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject bean = jsonArray.getJSONObject(i);
                    String News = bean.getString("news");
                    String ImgPath = bean.getString("imgPath");
                    String Name = bean.getString("name");
                    String UserName = bean.getString("userName");
                    NewsModel usernews = new NewsModel(News, UserName, Name, ImgPath);
                    list.add(usernews);
                }
//                System.out.println(list.get(0).getName());
                Message msg = Message.obtain();
                msg.what = Send_List;
                msg.obj = list;
                handler.sendMessage(msg);
//                jsonObject.


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class GetOwnNewsThread implements Runnable {
        private String username;
        public GetOwnNewsThread(String username) {
            this.username = username;
        }
        public void run() {
            get(username);
        }
    }


}