package com.example.chat.person.ui.home;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.chat.DataBase.database;
import com.example.chat.R;

import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.chat.JsonUtils.JsonUtil;
import com.example.chat.login.UserModel;
import com.example.chat.person.PersonOwnInformation;
import com.example.chat.person.group.groupUtil;
import com.example.chat.person.personInfo;
/*
* 数据结构:
* map(String:组名, List<UserModel>:用户信息)
* List<UserModel>
*
* */




public class HomeFragment extends Fragment {
    private static final int MSG_SUCCESS = 1, MSG_FAILED = 0;
    database dbOperator;
    TextView textView1;
    Button button;
    Handler handler;
    private EditText edit_modify;
    JsonUtil op = new JsonUtil();
    public static SharedPreferences sharedPreferences;
    ExpandableListView expandableListView;
    public static List<String> parentList;
    public static PersonAdapter adapter;
    private ModifyDialog dialog;
    public static String dataMap, dataParentList;
    public static SharedPreferences.Editor editor;
    private static Map<String, List<FriendListUser>> map;
    private HomeViewModel homeViewModel;
//    MyListener callBackValue;

    //定义回调接口
//    public interface MyListener{
//        public void sendValue(FriendListUser value);
//    }



//    private MyListener myListener;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this.getActivity(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String result = sharedPreferences.getString("username", "default");
        String ImgPath = sharedPreferences.getString("imgpath","default");
        Thread  thread = new Thread(new GetDataThread(result));
        thread.start();
//        button = (Button)root.findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch(msg.what) {
                    case MSG_SUCCESS : {
                        Map<String, Object> UserMap = (Map<String, Object>) msg.obj;
                        List<String> list = (List<String>) UserMap.get("group");
                        Map<String, List<FriendListUser>> map = (Map<String, List<FriendListUser>>) UserMap.get("information");
                        expandableListView = (ExpandableListView) root.findViewById(R.id.expandablelistview);

                        adapter = new PersonAdapter(getActivity().getApplicationContext(), list, map);
                        expandableListView.setAdapter(adapter);
                        //设置子项点击事件
                        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                FriendListUser User = map.get(list.get(groupPosition)).get(childPosition);
                                String[] UserString = new String[]{User.getUserName(),
                                        User.getName(),
                                        User.getImgPath(),
                                        User.getEmail(),
                                        User.getPassword(),
                                        User.getFriendgroup(),
                                        result,ImgPath,"true"};
//                                myListener.sendValue(User);
                                Intent intent = new Intent(getActivity(), PersonOwnInformation.class);
                                intent.putExtra("realuser", UserString);
                                startActivity(intent);

//                                Toast.makeText(getActivity(), "1111111" + list.get(0), Toast.LENGTH_SHORT);
                                return false;
                            }
                        });

                        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) { //长按子项
                                    return true;
                                }
                                else if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) { //长按分组标签
                                    Intent intent = new Intent(getActivity().getApplicationContext(), groupUtil.class);
                                    startActivity(intent);

                                }
                                return false;
                            }
                        });

                        break;
                    }
                    case MSG_FAILED : {
                        Toast.makeText(getActivity(), "failed!", Toast.LENGTH_SHORT);
                        break;
                    }
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
            }
        };
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
//        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        StringBuilder result = new StringBuilder();
        //学院无线:10.31.18.228   寝室无线:192.168.0.105
        String url = "http://120.79.114.234/project1/Servlet";
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
//                JSONObject jsonObject1 = jsonObject.getJSONObject("param");
//                String name = jsonObject1.getString("name");
//                String username = jsonObject1.getString("username");
                Map<String, Object> map = op.GetPerson(jsonObject);
                Message message = Message.obtain();
                message.obj = map;
                message.what = MSG_SUCCESS;
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

    class GetDataThread implements Runnable {
        private String username;
        public GetDataThread(String username) {
            this.username = username;
        }
        public void run() {
            send(username);
        }
    }




    //新增组
    public static void addGroup(String newGroupName) {
        parentList.add(newGroupName);
        List<FriendListUser> list = new ArrayList<>();
        map.put(newGroupName, list);
        adapter.notifyDataSetChanged();
        saveData();
    }

    //新增子项到指定组
    public static void addChild(int groupPosition, FriendListUser newChild) {
        String groupName = parentList.get(groupPosition);
        List<FriendListUser> list = map.get(groupName);
        list.add(newChild);
        adapter.notifyDataSetChanged();
        saveData();
    }

    //删除指定组
    public static void deleteGroup(int groupPos) {
        String groupName = parentList.get(groupPos);
        map.remove(groupName);
        parentList.remove(groupPos);
        adapter.notifyDataSetChanged();
        saveData();
    }

    //删除指定子项
    public static void deleteChild(int groupPos, int childPos) {
        String groupName = parentList.get(groupPos);
        List<FriendListUser> list = map.get(groupName);
        list.remove(childPos);
        adapter.notifyDataSetChanged();
        saveData();
    }



//    //弹修改对话框
//    public void alertModifyDialog(String title, String name) {
//        dialog = new ModifyDialog(getActivity(), title, name);
//        edit_modify = dialog.getEditText();
//        dialog.setOnClickCommitListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                modifyName(currentGroup, currentChild, edit_modify.getText().toString());
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

    //弹新增组对话框
    public void alertAddDialog(Context context, String title) {
        dialog = new ModifyDialog(context, title, null);
        edit_modify = dialog.getEditText();
        dialog.setOnClickCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup(edit_modify.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //activity和fragment联系时候调用，fragment必须依赖activty
//    @Override
//    public void onAttach(Context context) {
//        // TODO Auto-generated method stub
//        super.onAttach(context);
//        //当前fragment从activity重写了回调接口  得到接口的实例化对象
//        callBackValue = (MyListener) getActivity();
//    }

    //保存数据
    public static void saveData() {
        JSONObject jsonObject = new JSONObject(map);
        dataMap = jsonObject.toString();
        dataParentList = parentList.toString();

        editor = sharedPreferences.edit();
        editor.putString("dataMap", dataMap);
        editor.putString("dataParentList", dataParentList);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreate(null);
    }

}



