package com.example.chat.JsonUtils;

import com.example.chat.login.UserModel;
import com.example.chat.person.ui.home.FriendListUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    //取出jsonArray的值
    List<String> list = new ArrayList<>();

    public JSONObject CreateUpdateJson(String Name, String UserName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", UserName);
            jsonObject.put("name", Name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject CreateUserJson(UserModel User) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", User.getUserName());
            jsonObject.put("password", User.getPassword());
            jsonObject.put("name", User.getName());
            jsonObject.put("imagepath", User.getImgPath());
            jsonObject.put("email", User.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public List<String> GetMessage(JSONObject jsonObject, String ResultLabel) {
        try {
            JSONObject jsonObject1 = jsonObject.getJSONObject("param");
            JSONArray jsonArray = jsonObject1.getJSONArray("result");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject bean = jsonArray.getJSONObject(i);
                String element = bean.getString("message");
                list.add(element);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    // Map<String, Object>
    public Map<String, Object> GetPerson(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        Map<String, List<FriendListUser>> GroupMap = new HashMap<>();
        JSONArray JSgrouplist = new JSONArray();
        List<String> grouplist = new ArrayList<>();
        try {
            //获取好友信息
            JSgrouplist = (JSONArray) jsonObject.get("group");
            JSONObject jsonObject1 = jsonObject.getJSONObject("information");
            JSONArray jsonArray = jsonObject1.getJSONArray("result");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject bean = jsonArray.getJSONObject(i);
                String name = bean.getString("name");
                String username = bean.getString("username");
                String password = bean.getString("password");
                String imagepath = bean.getString("imagepath");
                String email = bean.getString("email");
                String friendgroup = bean.getString("friendgroup");
                FriendListUser user = new FriendListUser(username,password, name, email, imagepath, friendgroup);
                if(GroupMap.containsKey(friendgroup)) {
                    GroupMap.get(friendgroup).add(user);
                } else {
                    List<FriendListUser> list = new ArrayList<>();
                    list.add(user);
                    GroupMap.put(friendgroup, list);
                }

            }

            for(int i = 0; i < JSgrouplist.length(); i++) {
                grouplist.add(JSgrouplist.getString(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        map.put("information", GroupMap);
        map.put("group", grouplist);

        return map;
    }

    public List<String> GetGroupList(JSONObject jsonObject) {
        List<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("group");
            for(int i = 0; i < jsonArray.length(); i++) {
                String bean = (String) jsonArray.get(i);
                list.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
