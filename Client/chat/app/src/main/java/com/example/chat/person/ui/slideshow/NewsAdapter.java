package com.example.chat.person.ui.slideshow;




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
import com.example.chat.person.addFriend.AllUserAdapter;

public class NewsAdapter extends BaseAdapter {
    String group;
    private LayoutInflater layoutInflater;
    private List<NewsModel> AllUserList;
    private String UserName;
    private Context context;
    private BitMapShape op;
    public NewsAdapter(Context context, List<NewsModel> AllUserList, String UserName) {
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
        String NowName = AllUserList.get(position).getName();
        String NowNews = AllUserList.get(position).getNews();
        view = layoutInflater.inflate(R.layout.slide_item,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.slide_item_Icon);
        Bitmap bitmap = BitmapFactory.decodeFile(AllUserList.get(position).getImgPath()); //Path
        op = new BitMapShape(bitmap);
        Bitmap out = op.getCirleBitmap();
        imageView.setImageBitmap(out);
        TextView UserName, Name, News;
        UserName = (TextView) view.findViewById(R.id.slide_item_username);
        UserName.setText(NowUser);
        Name = (TextView) view.findViewById(R.id.slide_item_name);
        Name.setText(NowName);
        News = (TextView) view.findViewById(R.id.slide_item_news);
        News.setText(NowNews);
        return view;
    }
}


