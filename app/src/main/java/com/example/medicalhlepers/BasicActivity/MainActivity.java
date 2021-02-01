package com.example.medicalhlepers.BasicActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.MyApplication;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;
import com.example.medicalhlepers.RecordHelper.AudioRecordActivity;
import com.example.medicalhlepers.RecordHelper.AudioRecordJumpUtil;
import com.example.medicalhlepers.Test.Voice_wakeupTool;
import com.example.medicalhlepers.ThreeMainFragment.FragmentFind;
import com.example.medicalhlepers.ThreeMainFragment.FragmentHome;
import com.example.medicalhlepers.ThreeMainFragment.FragmentLuntan;
import com.example.medicalhlepers.ThreeMainFragment.FragmentMessage;
import com.example.medicalhlepers.Tools.BottomNavigationViewHelper;
import com.example.medicalhlepers.Tools.DragFloatActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private List<PersonalMessageStore> list;
    private PersonalMessageStore personalMessageStore;
    private Fragment firstFragment = new FragmentMessage();
    private Fragment secondFragment = new FragmentFind();
    private Fragment thirdFragment = new FragmentHome();
    private Fragment fourthFragment = new FragmentLuntan();
    private Voice_wakeupTool voice_wakeupTool;
    private WindowManager wm=null;
    private WindowManager.LayoutParams wmParams=null;
    private de.hdodenhof.circleimageview.CircleImageView imageView;

    // 设置默认进来是tab 显示的页面
    private void setDefaultFragment(){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, firstFragment);
        transaction.add(R.id.container, secondFragment);
        transaction.add(R.id.container, thirdFragment);
        transaction.add(R.id.container, fourthFragment);
        transaction.hide(secondFragment).hide(thirdFragment).hide(fourthFragment).show(firstFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.hide(firstFragment).hide(thirdFragment).hide(fourthFragment).show(thirdFragment).commit();
                    return true;
                case R.id.navigation_dashboard:
                    transaction.hide(firstFragment).hide(thirdFragment).hide(fourthFragment).show(secondFragment).commit();
                    return true;
                case R.id.navigation_notifications:
                    transaction.hide(secondFragment).hide(thirdFragment).hide(fourthFragment).show(firstFragment).commit();
                    return true;
                case R.id.navigation_luntan:
                    transaction.hide(secondFragment).hide(thirdFragment).hide(firstFragment).show(fourthFragment).commit();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
        }
        voice_wakeupTool = new Voice_wakeupTool(this);
        //voice_wakeupTool.start();
        initPersonalInfor();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setDefaultFragment();

        if (FloatWindow.get() == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.fragment_fragment_home, null);
            Button button = new Button(getApplicationContext());
            button.setBackgroundResource(R.drawable.btn_circle);
            button.setBackgroundResource(R.drawable.yuyin);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {   //悬浮按钮的点击事件
                    AudioRecordJumpUtil.startRecordAudio(MainActivity.this);
                }
            });
            FloatWindow
                    .with(getApplicationContext())
                    .setWidth(130)
                    .setHeight(130)
                    .setView(button)
                    .setX(100)    //设置控件初始位置
                    .setY(Screen.height,0.3f)
                    .setDesktopShow(true)   //桌面是否显示
                    .setMoveType(MoveType.slide)
                    .setFilter(false)   //app中不显示的页面
                    .build();
        }
        if (FloatWindow.get() != null) {
            FloatWindow.get().show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //voice_wakeupTool.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //voice_wakeupTool.stop();
    }

    private void initPersonalInfor() {  //在程序运行一开始，便获取个人信息
        list = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = list.get(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/getUserInfo").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                    String name, sex, idType, idNumber, birthday;
                    name = jsonObject.getString("name");
                    sex = jsonObject.getString("sex");
                    idType = jsonObject.getString("idType");
                    idNumber = jsonObject.getString("idNumber");
                    birthday = jsonObject.getString("birthday");
                    personalMessageStore.setUserName(name);
                    personalMessageStore.setUserSex(sex);
                    personalMessageStore.setIdType(idType);
                    personalMessageStore.setIdNumber(idNumber);
                    personalMessageStore.setUserbirthDay(birthday);
                    personalMessageStore.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

