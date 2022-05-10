package com.example.chat.person;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.chat.DataBase.database;
import com.example.chat.ViewShape.BitMapShape;
import com.example.chat.login.MainActivity;
import com.example.chat.news.CreateNews;
import com.example.chat.person.addFriend.AddFriend;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.example.chat.person.ui.home.HomeFragment;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class Information extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    database dbOperator;
    Handler handler;
    BitMapShape op;
    private final static int Send_Img = 1;
    String u, e, Img, Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWritePermission();
        dbOperator = new database(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(Information.this, MainActivity.class);
                startActivity(intent);
                finish();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        u = sharedPreferences.getString("username", "username");
        e = sharedPreferences.getString("email", "email");
        Img = sharedPreferences.getString("imgpath", "imgpath");
        Name = sharedPreferences.getString("name", "name");
        View header = navigationView.getHeaderView(0);
        TextView us = header.findViewById(R.id.HeaderUserName), em = header.findViewById(R.id.HeaderUserEmail);
        us.setText(u); em.setText(e);
        Thread thread = new Thread(new GetImgThread(u));
        thread.start();
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String Path = (String) msg.obj;
                switch (msg.what) {
                    case Send_Img : {
                        ImageView imageView = header.findViewById(R.id.HeaderImage);
                        Bitmap bitmap = BitmapFactory.decodeFile(Path);
                        op = new BitMapShape(bitmap);
                        Bitmap out = op.getCirleBitmap();
                        imageView.setImageBitmap(out);
                        HomeFragment SendToHome = new HomeFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("username", u);
                        SendToHome.setArguments(bundle);
                        break;
                    }
                }
            }

        };

        //待替换(mysql+servlet)
//        String Path = dbOperator.query(u, "imagepath");
        //

//        Toast.makeText(Information.this, "111111" + Path, Toast.LENGTH_LONG).show();
//        ImageView imageView = header.findViewById(R.id.HeaderImage);
//        Bitmap bitmap = BitmapFactory.decodeFile(Path);
//        op = new BitMapShape(bitmap);
//        Bitmap out = op.getCirleBitmap();
//        imageView.setImageBitmap(out);
//        HomeFragment SendToHome = new HomeFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("username", u);
//        SendToHome.setArguments(bundle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.organization : {
                Toast.makeText(this, "功能暂未开发,敬请期待.", Toast.LENGTH_LONG).show();
                break;
            }

            case R.id.addfriend : {
                Intent intent = new Intent(Information.this, AddFriend.class);
                startActivity(intent);
//                Toast.makeText(this, "添加好友", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.addnews : {
                Intent intent = new Intent(Information.this, CreateNews.class);
                String[] args = new String[]{u, Img, Name};
                intent.putExtra("information", args);
                startActivity(intent);
            }
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void requestWritePermission(){
        if (ActivityCompat.checkSelfPermission(Information.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Information.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
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
        String url = "http://120.79.114.234/project1/GetImg";
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
                String ImgPath = jsonObject.getString("imgpath");
                Message msg = Message.obtain();
                msg.what = Send_Img;
                msg.obj = ImgPath;
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
