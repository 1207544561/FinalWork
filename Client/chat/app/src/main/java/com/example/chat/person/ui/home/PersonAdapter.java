package com.example.chat.person.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.example.chat.R;
import com.example.chat.ViewShape.BitMapShape;
import com.example.chat.login.UserModel;

/**
 * Created by lw on 2017/4/14.
 */

//public class PersonAdaptor extends ArrayAdapter {
//    private final int resourceId;
//    BitMapShape op;
//    private Context context;
//    public PersonAdaptor(Context context, int textViewResourceId, List<UserModel> objects) {
//        super(context, textViewResourceId, objects);
//        this.context = context;
//        resourceId = textViewResourceId;
//    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        UserModel user = (UserModel) getItem(position); // 获取当前项的Fruit实例
//        View view = LayoutInflater.from(this.context).inflate(resourceId, null);//实例化一个对象
//        ImageView PersonImage = (ImageView) view.findViewById(R.id.person_image);//获取该布局内的图片视图
//        TextView PersonName = (TextView) view.findViewById(R.id.person_name);//获取该布局内的文本视图
//        Bitmap bitmap = BitmapFactory.decodeFile(user.getImgPath());
//        op = new BitMapShape(bitmap);
//        Bitmap out = op.getCirleBitmap();
//        PersonImage.setImageBitmap(out);
//        PersonName.setText(user.getName());//为文本视图设置文本内容
//        return view;
//    }
//}

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.chat.ViewShape.BitMapShape;

public class PersonAdapter extends BaseExpandableListAdapter {
    private List<String> parentList;
    private Map<String,List<FriendListUser>> map;
    private Context context;
    private EditText edit_modify;
    private ModifyDialog dialog;
    private BitMapShape op;

    //构造函数
    public PersonAdapter(Context context, List<String> parentList, Map<String,List<FriendListUser>> map) {
        this.context = context;
        this.parentList = parentList;
        this.map = map;

    }

    //获取分组数
    @Override
    public int getGroupCount() {
        return parentList.size();
    }
    //获取当前组的子项数
    @Override
    public int getChildrenCount(int groupPosition) {
        String groupName = parentList.get(groupPosition);
        if(map.get(groupName) == null){
            List<FriendListUser> list = new ArrayList<>();
            map.put(groupName, list);
        }
        int childCount = map.get(groupName).size();
        return childCount;
    }
    //获取当前组对象
    @Override
    public Object getGroup(int groupPosition) {
        String groupName = parentList.get(groupPosition);
        return groupName;
    }
    //获取当前子项对象
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String groupName = parentList.get(groupPosition);
        String childlName = map.get(groupName).get(childPosition).getName();
        return childlName;
    }

    //获取组ID
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    //获取子项ID
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    //组视图初始化
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final int groupPos = groupPosition;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_parent, null);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.image_parent);

        if(isExpanded){
            image.setImageResource(R.drawable.image_parent2);
        }else{
            image.setImageResource(R.drawable.image_parent1);
        }



        TextView parentText = (TextView) convertView.findViewById(R.id.text_parent);
        parentText.setText(parentList.get(groupPosition));
        return convertView;
    }

    //子项视图初始化
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final int groupPos = groupPosition;
        final int childPos = childPosition;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.person_item, null);
        }
        TextView childText = (TextView) convertView.findViewById(R.id.person_name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.person_image);
        childText.setGravity(Gravity.CENTER);
        String parentName = parentList.get(groupPosition);
        String childName = map.get(parentName).get(childPosition).getName();
        childText.setText(childName);
        String Path = map.get(parentName).get(childPosition).getImgPath();
        Bitmap bitmap = BitmapFactory.decodeFile(Path);
        op = new BitMapShape(bitmap);
        Bitmap out = op.getCirleBitmap();
        imageView.setImageBitmap(out);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
