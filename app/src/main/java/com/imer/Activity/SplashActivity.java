package com.imer.Activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.imer.R;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by 丶 on 2017/3/11.
 */

public class SplashActivity extends Activity {

    private SharedPreferences prefs;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);
        textView = (TextView) findViewById(R.id.tv_splash);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLogin = prefs.getBoolean("islogin", false);
                if (isLogin) {
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, textView, "sharedTextView").toBundle());
                } else {
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(intent);
                }
                finishAfterTransition();
            }
        }, 1000);
    }
}
