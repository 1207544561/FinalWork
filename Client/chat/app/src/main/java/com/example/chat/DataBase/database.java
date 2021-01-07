package com.example.chat.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.chat.login.UserModel;

public class database {
    String  password, result;
    byte[] img;
    private DatabaseHelper databaseHelper;

    public database(Context context) {
        this.databaseHelper = new DatabaseHelper(context, "Chat.db", null, 1);
    }

    //插入
    public void insert(UserModel model) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", model.getUserName());
        cv.put("password", model.getPassword());
        cv.put("name", model.getName());
        cv.put("email", model.getEmail());
        cv.put("imagepath", model.getImgPath());
        db.insert("user", null, cv);
        db.close();
    }

    //删除
    public void delete(UserModel model) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String condition = "username=?";
        String[] cause = {model.getUserName()};
        db.delete("user", condition, cause);
        db.close();
    }

    //修改
    public void update(String username, String column, String value) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(column,value);
        String condition = "username=?";
        String[] cause={username};
        db.update("user", cv, condition, cause);
        db.close();
    }

    //查询用户密码
    public boolean querypwd(String UserName, String Password) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //设置游标查询
        Cursor cursor = db.query ("user",
                null,
                "username=?",
                new String[] {UserName},
                null,
                null,
                null);

        //密码错误
        if(cursor.moveToFirst()) {
            do{
                password = (String)cursor.getString(cursor.getColumnIndex("password"));
            }   while(cursor.moveToNext());
            if(password.equals(Password)) return true;
            return false;
        }
        return false;
    }

    public int getCount(String UserName) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //设置游标查询
        Cursor cursor = db.query ("user",
                null,
                "username=?",
                new String[] {UserName},
                null,
                null,
                null);
        return cursor.getCount();
    }

    //查询信息
    public  String query(String UserName, String aim) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("user",
                null,
                "username=?",
                new String[]{UserName},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(cursor.getColumnIndex(aim));
            } while (cursor.moveToNext());
        }
        return result;
    }

}
