package com.example.chat.person.ui.gallery;


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

public class UserMessageAdapter extends BaseAdapter {
    String group;
    private LayoutInflater layoutInflater;
    private List<UserAndMessageModel> AllUserList;
    private String UserName;
    private Context context;
    private BitMapShape op;
    public UserMessageAdapter(Context context, List<UserAndMessageModel> AllUserList, String UserName) {
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
        String NowMessage = AllUserList.get(position).getMessage();
        view = layoutInflater.inflate(R.layout.message_item,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.message_item_icon);
        Bitmap bitmap = BitmapFactory.decodeFile(AllUserList.get(position).getImgPath()); //Path
        op = new BitMapShape(bitmap);
        Bitmap out = op.getCirleBitmap();
        imageView.setImageBitmap(out);
        TextView UserName, Message;
        UserName = (TextView) view.findViewById(R.id.message_item_name); Message = (TextView) view.findViewById(R.id.message_item_message);
        UserName.setText(NowUser); Message.setText(NowMessage);
        return view;
    }
}

