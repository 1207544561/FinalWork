package com.example.chat.person.ui.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.chat.R;
import com.example.chat.message.MessageMain;

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

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    SharedPreferences sharedPreferences;
    Handler handler;
    private static final int Send_User = 18;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String result = sharedPreferences.getString("username", "default");
        String ImgPath = sharedPreferences.getString("imgpath","default");
        Thread thread = new Thread(new GetDataThread(result));
        thread.start();

        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                List<UserAndMessageModel> list = (List<UserAndMessageModel>) msg.obj;
                switch (msg.what) {
                    case Send_User : {
                        ListView listView = (ListView) getActivity().findViewById(R.id.gallery_listview);
                        UserMessageAdapter adapter = new UserMessageAdapter(getActivity().getApplicationContext() ,list, result);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getActivity(), MessageMain.class);
                                String[] UserString = new String[] {
                                        list.get(position).getUserName(),
                                        list.get(position).getName(),
                                        list.get(position).getImgPath(),
                                        list.get(position).getEmail(),
                                        list.get(position).getPassword(),
                                        list.get(position).getFriendgroup(),
                                        result, ImgPath, "true"};
                                intent.putExtra("nowuser", UserString);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

        };
//        UserMessageAdapter adapter = new UserMessageAdapter()
//        final TextView textView = root.findViewById(R.id.text_gallery);
//        galleryViewModel.getText().observe(this, new Observer<String>() {
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

    public void send(String username) {
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/GetAllMessage";
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
                JSONArray jsonArray = jsonObject.getJSONArray("reallist");
                List<UserAndMessageModel> list = new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject bean = jsonArray.getJSONObject(i);
                    UserAndMessageModel userbean = new UserAndMessageModel(
                            bean.getString("userName"),
                            bean.getString("password"),
                            bean.getString("name"),
                            bean.getString("email"),
                            bean.getString("imgPath"),
                            bean.getString("group"),
                            bean.getString("message")
                    );
                    list.add(userbean);
                }

                Message msg = Message.obtain();
                msg.what = Send_User;
                msg.obj = list;
                handler.sendMessage(msg);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class GetDataThread implements Runnable {
        private String username;
        public GetDataThread(String username) {
            this.username = username;
        }
        public void run() {
            send(username);
        }
    }

}