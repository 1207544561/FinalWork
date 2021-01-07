package com.example.chat.person.ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.chat.R;

public class SendFragment extends Fragment {

    private SendViewModel sendViewModel;
    ImageView coin, have, like;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
//        final TextView textView = root.findViewById(R.id.text_send);
//        sendViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        coin = root.findViewById(R.id.coin);
        coin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coin.setImageDrawable(getResources().getDrawable(R.drawable.coin_activate));
            }
        });

        have = root.findViewById(R.id.have);
        have.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                have.setImageDrawable(getResources().getDrawable(R.drawable.have_activate));
            }
        });

        like = root.findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like.setImageDrawable(getResources().getDrawable(R.drawable.thumb_activate));
            }
        });

        return root;
    }
}