package com.example.medicalhlepers.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.BasicActivity.MainActivity;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    private PersonalMessageStore personalMessageStore;
    private Button registerButton;
    private Button loginButton;
    private EditText phoneInput;
    private EditText passwordInput;
    private CheckBox rememberPass;
    private String account = "";
    private String password = "";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public static final int WARNING_TXT = 1;
    public static final int SUCCESS_TXT = 0;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WARNING_TXT:
                    Toast.makeText(Login.this, "密码错误",
                            Toast.LENGTH_LONG).show();
                    break;
                case SUCCESS_TXT:
                    editor = pref.edit();
                    if(rememberPass.isChecked()) {
                        editor.putString("account", account);
                        editor.putString("password", password);
                    } else {
                        editor.clear();
                    }
                    editor.apply();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneInput = (EditText) findViewById(R.id.et_account);
        passwordInput = (EditText) findViewById(R.id.et_pwd);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        pref  = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if(isRemember) {    //是否选择过记住密码?
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            phoneInput.setText(account);
            passwordInput.setText(password);
            rememberPass.setChecked(true);
        }
        initHospitalAddress();
        //获取数据库中的用户信息
        registerButton = (Button) findViewById(R.id.btn_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        //登录
        loginButton = (Button) findViewById(R.id.btn_submit);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = phoneInput.getText().toString();
                password = passwordInput.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            OkHttpClient client = new OkHttpClient();
                            //发送数据
                            RequestBody requestBody = new FormBody.Builder().add("phone", account)
                                    .add("passwd",password).build();
                            Request request = new Request.Builder().url("http://39.96.41.6:8080/login").post(requestBody)
                                    .build();
                            //获取数据
                            Response response = client.newCall(request).execute();
                            String responseData=response.body().string();
                            JSONArray jsonArray=new JSONArray(responseData);
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            String result=jsonObject.getString("result");
                            if (result.equals("登录失败")){    //登录失败
                                Log.d("Login", "失败");
                                gotoMain(false);
                                Message message = new Message();
                                message.what = WARNING_TXT;
                                handler.sendMessage(message);
                                //Toast.makeText(Login.this, "密码错误",
                                        //Toast.LENGTH_LONG).show();
                            }
                            else{                      //登陆成功
                                Log.d("Login", "成功");
                                //每次登录成功，都清空过去的本地用户数据库，更新为当前登录用户的数据
                                DataSupport.deleteAll(PersonalMessageStore.class);
                                PersonalMessageStore personalMessageStore = new PersonalMessageStore();
                                personalMessageStore.setPhoneNumber(account);
                                personalMessageStore.setPassword(password);
                                personalMessageStore.save();
                                Message message = new Message();
                                message.what = SUCCESS_TXT;
                                handler.sendMessage(message);
                                gotoMain(true);
                            }
                            //注意，网络访问必须在子线程中进行，要更改UI界面必须回到主线程，方法自己百度。。。
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    public void initHospitalAddress() {
        LitePal.getDatabase();
    }

    public void gotoMain(boolean choice) {
        if(choice) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
