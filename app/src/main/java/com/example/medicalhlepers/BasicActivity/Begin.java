package com.example.medicalhlepers.BasicActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.Voice;
import android.view.Window;
import android.view.WindowManager;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.AIdiagnose.DiagonseResultMain;
import com.example.medicalhlepers.AIdiagnose.OtherSymptoms;
import com.example.medicalhlepers.Login.Login;
import com.example.medicalhlepers.PriceQuery.PriceQuery;
import com.example.medicalhlepers.Test.Voice_index;
import com.example.medicalhlepers.Test.Voice_wakeup;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.litepal.LitePal;

public class Begin extends AppCompatActivity {
    private SharedPreferences pref;
    private String account, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.getDatabase();
        Fresco.initialize(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_begin);
        pref  = getSharedPreferences("data", MODE_PRIVATE);
        handler.sendEmptyMessageDelayed(0,3000);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if(isRemember) {    //是否选择过记住密码?
            account = pref.getString("account", "");
            password = pref.getString("password", "");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getHome();
            super.handleMessage(msg);
        }
    };

    public void getHome() {
        //Intent intent = new Intent(Begin.this, MainActivity.class);

        Intent intent = new Intent(Begin.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

