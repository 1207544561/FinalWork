package com.example.chat.message;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.security.acl.Group;
import java.util.List;
import java.util.Map;

import com.example.chat.R;
import com.example.chat.ViewShape.BitMapShape;
import com.example.chat.login.UserModel;
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
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MessageAdapter extends BaseAdapter {
    String group;
    private LayoutInflater layoutInflater;
    private List<MessageModel> MessageList;
    private String UserName;
    private Context context;
    private BitMapShape op;
    public MessageAdapter(Context context, List<MessageModel> MessageList, String UserName) {
        this.MessageList = MessageList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.UserName = UserName;
    }

    @Override
    public int getCount() {
        return MessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return MessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        String NowUser = MessageList.get(position).getUser();
        if(NowUser.equals(UserName)) {
            view = layoutInflater.inflate(R.layout.right_item,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.r_img);
            Bitmap bitmap = BitmapFactory.decodeFile(MessageList.get(position).getUserImgPath()); //Path
            op = new BitMapShape(bitmap);
            Bitmap out = op.getCirleBitmap();
            imageView.setImageBitmap(out);
            TextView UserMessage = (TextView) view.findViewById(R.id.r_text);
            UserMessage.setText(MessageList.get(position).getMessageText());
        } else {
//            String[] UserString = new String[]{User.getUserName(),
//                    User.getName(),
//                    User.getImgPath(),
//                    User.getEmail(),
//                    User.getPassword(),
//                    User.getFriendgroup(),
//                    result};
////                                myListener.sendValue(User);
//            Intent intent = new Intent(getActivity(), PersonOwnInformation.class);
//            intent.putExtra("realuser", UserString);
//            startActivity(intent);

            view = layoutInflater.inflate(R.layout.left_item,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.l_img);
            Bitmap bitmap = BitmapFactory.decodeFile(MessageList.get(position).getUserImgPath()); //Path
            op = new BitMapShape(bitmap);
            Bitmap out = op.getCirleBitmap();
            imageView.setImageBitmap(out);
            TextView UserMessage = (TextView) view.findViewById(R.id.l_text);
            UserMessage.setText(MessageList.get(position).getMessageText());
        }

        return view;
    }


}
