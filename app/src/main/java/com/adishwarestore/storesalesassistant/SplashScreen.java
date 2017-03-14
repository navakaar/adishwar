package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by dhiraj on 04-Jan-17.
 */

public class SplashScreen extends Activity {
    // Splash screen timer
    //private static int SPLASH_TIME_OUT = 3000;
    private static int SPLASH_TIME_OUT = 1000;  // 1 sec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView splashTv = (TextView)findViewById(R.id.splash_Tv);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));
        splashTv.setTypeface(typeface);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                //Intent i = new Intent(SplashScreen.this, MainActivity.class);
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
