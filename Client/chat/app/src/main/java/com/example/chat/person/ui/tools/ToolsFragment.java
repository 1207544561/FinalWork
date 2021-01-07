package com.example.chat.person.ui.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.chat.R;
import com.example.chat.person.group.groupUtil;
import com.example.chat.person.ui.slideshow.NewsAdapter;

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

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    SharedPreferences sharedPreferences;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        TextView change_name = (TextView) root.findViewById(R.id.text_change_name);
        change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        TextView Version = (TextView) root.findViewById(R.id.text_version);
        Version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "版本号:Chat1.0", Toast.LENGTH_SHORT).show();
            }
        });



        return root;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            editText.setBackground(getResources().getDrawable(R.drawable.shape_textview));
        new AlertDialog.Builder(getActivity()).setTitle("修改名字")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String Name = editText.getText().toString();
                        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                        String UserName = sharedPreferences.getString("username", "default");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", Name);
                        editor.commit();
                        Thread thread = new Thread(new ChangeNameThread(UserName, Name));
                        thread.start();
//                        Thread thread = new Thread(new groupUtil.AddThread(result, group));
//                        thread.start();

                        //按下确定键后的事件
                        Toast.makeText(getActivity(), "修改成功",Toast.LENGTH_LONG).show();

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

    public void ChangeName(String UserName, String NewName) {
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://192.168.0.114:8080/project1/UpdateUserName";
        HttpPost httpPost = new HttpPost(url);
        try {
            JSONObject json = new JSONObject();
            json.put("username", UserName);
            json.put("name", NewName);
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

    class ChangeNameThread implements Runnable {
        String UserName, NewName;
        public ChangeNameThread(String UserName, String NewName) {
            this.UserName = UserName;
            this.NewName = NewName;
        }

        public void run() {
            ChangeName(UserName, NewName);
        }
    }




}