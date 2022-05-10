package com.example.chat.person.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chat.R;


public class ModifyDialog extends Dialog {
    private TextView text_title;
    private EditText edit_modify;
    private Button btn_commit;

    public ModifyDialog(Context context, String title, String name) {
        super(context, R.style.noTitleDialog);

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_modify, null);
        text_title = (TextView) view.findViewById(R.id.text_title);
        edit_modify = (EditText)view.findViewById(R.id.edit_modify);
        btn_commit = (Button) view.findViewById(R.id.btn_commit);
        text_title.setText(title);
        edit_modify.setText(name);

        super.setContentView(view);
    }

    public EditText getEditText(){
        return edit_modify;
    }

    public void setOnClickCommitListener(View.OnClickListener listener) {
        btn_commit.setOnClickListener(listener);
    }
}
