package com.example.passwordfeatures;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button buttonEnter;
    EditText editLogin, editPassword;
    TextView textInfo;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInfo = (TextView) findViewById(R.id.textInfo);
        buttonEnter = (Button) findViewById(R.id.buttonEnter);
        editLogin = (EditText) findViewById(R.id.editLogin);
        editPassword = (EditText) findViewById(R.id.editPassword);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File passwordFile = new File("password.bin");
                if (!passwordFile.exists())
                {
                    user = new User(editLogin.getText().toString(),editPassword.getText().toString());
                    user.serialisePassword();
                    user.getserialisePassword();
                    textInfo.setText(user.toString());
                }
                else{
                    textInfo.setText("все знаем " + user.toString());
                }
            }
        };
        buttonEnter.setOnClickListener(onClickListener);
    }
}
