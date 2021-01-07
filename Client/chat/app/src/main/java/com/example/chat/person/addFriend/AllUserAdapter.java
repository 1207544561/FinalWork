package com.example.chat.person.addFriend;




import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.chat.R;
import com.example.chat.ViewShape.BitMapShape;

public class AllUserAdapter extends BaseAdapter {
    String group;
    private LayoutInflater layoutInflater;
    private List<UserListModel> AllUserList;
    private String UserName;
    private Context context;
    private BitMapShape op;
    public AllUserAdapter(Context context, List<UserListModel> AllUserList, String UserName) {
        this.AllUserList = AllUserList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.UserName = UserName;
    }

    @Override
    public int getCount() {
        return AllUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return AllUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        String NowUser = AllUserList.get(position).getUserName();
        view = layoutInflater.inflate(R.layout.search_item,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.SearchItemIconImg);
        Bitmap bitmap = BitmapFactory.decodeFile(AllUserList.get(position).getImgPath()); //Path
        op = new BitMapShape(bitmap);
        Bitmap out = op.getCirleBitmap();
        imageView.setImageBitmap(out);
        TextView UserName;
        UserName = (TextView) view.findViewById(R.id.SearchItemNameText);
        UserName.setText(NowUser);
        return view;
    }

//    public HttpClient getHttpClient() { //获取HttpClient对象
//        BasicHttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
//        HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
//        HttpClient client = new DefaultHttpClient(httpParams);
//        return client;
//    }
//
//    private void showDialog() {
//        AlertDialog.Builder builder=new AlertDialog.Builder(context);
//        final EditText editText = new EditText(context);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//            editText.setBackground(context.getResources().getDrawable(R.drawable.shape_textview));
////        builder.setTitle("新建分组");
//
//        new AlertDialog.Builder(context).setTitle("新建分组")
//                .setView(editText)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        group = editText.getText().toString();
////                        Thread thread = new Thread(new com.example.chat.person.group.GroupAdapter.AddThread(UserName, group));
////                        thread.start();
//                        //按下确定键后的事件
////                        Toast.makeText(getApplicationContext(), editText.getText().toString(),Toast.LENGTH_LONG).show();
//
//                    }
//                }).setNegativeButton("取消",null).show();
//    }
//
//    public void add(String UserName, String NewGroupName) {
//        StringBuilder result = new StringBuilder();
//        //学院无线:10.31.18.228   寝室无线:192.168.0.105
//        String url = "http://192.168.0.107:8080/project1/CreateGroup";
//        HttpPost httpPost = new HttpPost(url);
//        try {
//            JSONObject json = new JSONObject();
//            json.put("username", UserName);
//            json.put("newgroup", NewGroupName);
//            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
//            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//            httpPost.setEntity(stringEntity);
//            HttpClient httpClient = getHttpClient();
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            System.out.println("发送成功!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void send(String GroupName) {
////        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
//        StringBuilder result = new StringBuilder();
//        //学院无线:10.31.18.228   寝室无线:192.168.0.105   三号ip:192.168.43.200
//        String url = "http://192.168.0.107:8080/project1/DeleteGroup";
//        HttpPost httpPost = new HttpPost(url);
//        try {
//            JSONObject json = new JSONObject();
//
//            json.put("groupname", GroupName);
//            json.put("username", UserName);
//
//            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
//            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//            httpPost.setEntity(stringEntity);
//            HttpClient httpClient = getHttpClient();
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            System.out.println("发送成功!");
//
////                JSONObject jsonObject1 = jsonObject.getJSONObject("param");
////                String name = jsonObject1.getString("name");
////                String username = jsonObject1.getString("username");
//
////                handler.obtainMessage(MSG_SUCCESS, list);
////                System.out.println(name + "   " + username);
////                System.out.println("返回的值是:" + name);
////                Iterator<String> it = list.iterator();
////                while (it.hasNext()) {
////                    System.out.println((String) it.next());
////                }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    class DeleteThread implements Runnable {
//        private String groupname;
//        public DeleteThread(String groupname) {
//            this.groupname = groupname;
//        }
//        public void run() {
//            send(groupname);
//        }
//    }
//
//    class AddThread implements Runnable {
//        String UserName, NewGroupName;
//        public AddThread(String UserName, String NewGroupName) {
//            this.UserName = UserName;
//            this.NewGroupName = NewGroupName;
//        }
//
//        public void run() {
//            add(UserName, NewGroupName);
//        }
//    }
}


