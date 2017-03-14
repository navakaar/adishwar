package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by dhirb on 14-03-2017.
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setScreenFonts();

        Button loginBtn = (Button)findViewById(R.id.al_loginBtn);
        loginBtn.setOnClickListener(this);
    }

    void setScreenFonts() {
        // Create Fonts
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));


        // Set Font
        TextInputLayout usernameTil = (TextInputLayout)findViewById(R.id.al_usernameWrapper_Til);
        TextInputLayout passwordTil = (TextInputLayout)findViewById(R.id.al_passwordWrapper_Til);
        usernameTil.setTypeface(typeface2); passwordTil.setTypeface(typeface2);

        TextView userTv = (TextView)findViewById(R.id.al_user_Tv);
        TextView pinTv = (TextView)findViewById(R.id.al_pin_Tv);
        userTv.setTypeface(typeface2); pinTv.setTypeface(typeface2);


        Button loginBtn = (Button)findViewById(R.id.al_loginBtn);
        TextView errMsgTv = (TextView)findViewById(R.id.al_loginErr_Tv);
        TextView forgotPwdTv = (TextView)findViewById(R.id.al_forgotPwd_Tv);
        TextView tncTv = (TextView)findViewById(R.id.al_tnc_Tv);
        loginBtn.setTypeface(typeface2); errMsgTv.setTypeface(typeface2);
        forgotPwdTv.setTypeface(typeface2); tncTv.setTypeface(typeface2);


        if (tncTv != null) {
            tncTv.setMovementMethod(LinkMovementMethod.getInstance());
        }

        return;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.al_loginBtn:
                //Toast.makeText(this, "Login Button Pressed", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
        }
    }
}
