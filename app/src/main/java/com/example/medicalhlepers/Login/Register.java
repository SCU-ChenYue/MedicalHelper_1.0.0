package com.example.medicalhlepers.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.BasicActivity.ChangePassword;
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

public class Register extends AppCompatActivity {
    private EditText phoneNumberInput;
    private EditText passwordInput;
    private EditText passwordInput2;
    private Button saveUser;
    private Button cancelButton;
    private ImageView back;
    public final static int FAIL_REGISTER = 1;
    private TextView log;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FAIL_REGISTER:
                    Toast.makeText(Register.this, "账号已存在",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final Intent intent = getIntent();
        phoneNumberInput = (EditText) findViewById(R.id.ed1);
        passwordInput = (EditText) findViewById(R.id.ed2);
        passwordInput2 = (EditText) findViewById(R.id.ed3);
        cancelButton = (Button) findViewById(R.id.btn_del);
        saveUser = (Button) findViewById(R.id.btn_submit);
        saveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;
                final String phoneNumber = phoneNumberInput.getText().toString();
                final String password1 = passwordInput.getText().toString();
                String password2 = passwordInput2.getText().toString();
                if(phoneNumber.equals("")) {
                    Toast.makeText(Register.this, "手机号不能为空",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else if(password1.equals("") && flag == 0) {
                    Toast.makeText(Register.this, "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else if(password2.equals("") && flag == 0) {
                    Toast.makeText(Register.this, "请再次输入密码",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else if(!password1.equals(password2) && flag == 0) {
                    Toast.makeText(Register.this, "请确保两次密码输入一致",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else if(checkForCorrect(password2)) { //如果密码不符合规范
                    Toast.makeText(Register.this, "请输入6-15位数字或字母",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                }
                if(flag == 0) { //新建一个用户，这个地方需要响应服务器并上传手机号和密码
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder().add("phone",
                                        phoneNumber).add("passwd",password1).build();
                                Request request = new Request.Builder().url("http://39.96.41.6:8080/register").
                                        post(requestBody).build();
                                Response response = client.newCall(request).execute();
                                String responseData=response.body().string();
                                JSONArray jsonArray=new JSONArray(responseData);
                                JSONObject jsonObject=jsonArray.getJSONObject(0);
                                String result=jsonObject.getString("result");
                                if(result.equals("注册成功")) {
                                    Log.d("Login", "注册成功");
                                } else {
                                    Log.d("Login", "已在");
                                    Message message = new Message();
                                    message.what = FAIL_REGISTER;
                                    handler.sendMessage(message);
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    finish();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //返回
        back = (ImageView) findViewById(R.id.login_titleBar_iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //登陆
        log = (TextView) findViewById(R.id.login_titleBar_tv_register);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    public static boolean checkForCorrect(String password) {
        if(password.length() < 6 || password.length() > 15) {
            return true;
        }
        for(int i = 0; i < password.length(); i++) {
            char a = password.charAt(i);
            if((a > '0' && a < '9') || (a > 'a' && a < 'z')) {
                continue;
            } else {
                return true;
            }
        }
        return false;
    }
}
