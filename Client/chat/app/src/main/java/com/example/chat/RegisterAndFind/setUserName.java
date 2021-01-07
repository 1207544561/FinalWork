package com.example.chat.RegisterAndFind;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.DataBase.database;
import com.example.chat.JsonUtils.JsonUtil;
import com.example.chat.R;
import com.example.chat.ViewShape.BitMapShape;
import com.example.chat.login.MainActivity;
import com.example.chat.login.UserModel;

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

public class setUserName extends AppCompatActivity {
    ImageView UserImg;
    Intent intent;
    String Username;
    BitMapShape op;
    JsonUtil jsonUtil = new JsonUtil();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_setusername);
        intent = getIntent();
        Username = intent.getStringExtra("username");
        /*
        测试
        Intent intent = getIntent();
        String Username = intent.getStringExtra("username");
        Toast.makeText(setUserName.this, Username, Toast.LENGTH_SHORT).show();
        */
        final database dbOperator = new database(this);
        Button SubmitButton = (Button)findViewById(R.id.submit);
        UserImg = (ImageView)findViewById(R.id.UserPict);
        String Path = dbOperator.query(Username, "imagepath");
        Bitmap bitmap = BitmapFactory.decodeFile(Path);
        op = new BitMapShape(bitmap);
        Bitmap out = op.getCirleBitmap();
        UserImg.setImageBitmap(out);
//        UserImg.setImageBitmap(BitmapFactory.decodeFile(Path));
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText UserName = (EditText)findViewById(R.id.UserNameText);
                String UserNameText = UserName.getText().toString();
                Thread thread = new Thread(new SendUpdateThread(UserNameText));
                thread.start();
                dbOperator.update(Username, "name", UserNameText);
                Intent JumpIntent = new Intent(setUserName.this, MainActivity.class);
                startActivity(JumpIntent);
                finish();

            }
        });
    }

    public HttpClient getHttpClient(){ //获取HttpClient对象
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    public void send(String Name) {
        //向Servlet发送数据用于操作数据库
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/UpdateUserName";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json = jsonUtil.CreateUpdateJson(Name, Username);
            StringEntity stringEntity = new StringEntity(json.toString());
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            System.out.println("发送成功!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SendUpdateThread implements Runnable {
        String Name;
        public SendUpdateThread(String Name) {
            this.Name = Name;
        }
        public void run() {
            send(Name);
        }
    }

}