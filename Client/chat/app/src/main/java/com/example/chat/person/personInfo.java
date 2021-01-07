package com.example.chat.person;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.DataBase.database;
import com.example.chat.R;
import com.example.chat.login.MainActivity;

public class personInfo extends AppCompatActivity {
    TextView UserName, Password, Name, Email;
    String UserNameText, PasswordText, NameText, EmailText;
    Button logout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personinfo);
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        UserNameText = sharedPreferences.getString("username", null);
        PasswordText = sharedPreferences.getString("password", null);
        NameText = sharedPreferences.getString("name", null);
        EmailText = sharedPreferences.getString("email", null);
        UserName = (TextView)findViewById(R.id.InfoUsername); Name = (TextView)findViewById(R.id.InfoName);
        Password = (TextView)findViewById(R.id.InfoPassword); Email = (TextView)findViewById(R.id.InfoEmail);
        UserName.setText(UserNameText); Name.setText(NameText); Password.setText(PasswordText); Email.setText(EmailText);
        logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(personInfo.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        /*
        测试
        Intent intent = getIntent();
        String Username = intent.getStringExtra("username");
        Toast.makeText(setUserName.this, Username, Toast.LENGTH_SHORT).show();
        */

    }
}