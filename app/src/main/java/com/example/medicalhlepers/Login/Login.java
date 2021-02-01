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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.AIdiagnose.Setting;
import com.example.medicalhlepers.BasicActivity.MainActivity;
import com.example.medicalhlepers.Dialog.MyProgressDialog;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

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
    private ImageView back;
    private SharedPreferences pref;
    private List<PersonalMessageStore> list;
    private TextView Regis;
    private SharedPreferences.Editor editor;
    private MyProgressDialog dialog;
    public static final int WARNING_TXT = 1;
    public static final int SUCCESS_TXT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        phoneInput = (EditText) findViewById(R.id.et_account);
        passwordInput = (EditText) findViewById(R.id.et_pwd);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        pref  = getSharedPreferences("data", MODE_PRIVATE);
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
                showMyDialog(v);
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
                                dialog.dismiss();
                                gotoMain(false);
                                //Toast.makeText(Login.this, "密码错误",
                                        //Toast.LENGTH_LONG).show();
                            }
                            else{                      //登陆成功
                                Log.d("Login", "成功");
                                //每次登录成功，都清空过去的本地用户数据库，更新为当前登录用户的数据
                                DataSupport.deleteAll(PersonalMessageStore.class);
                                DataSupport.deleteAll(Setting.class);
                                PersonalMessageStore personalMessageStore = new PersonalMessageStore();
                                personalMessageStore.setPhoneNumber(account);
                                personalMessageStore.setPassword(password);
                                personalMessageStore.save();

                                Setting setting = new Setting();
                                setting.setHospitalchooseType("");
                                setting.setHospitalchooseLevel("");
                                setting.setDocchooseLevel("");
                                setting.setDistance(100);
                                setting.setDistancePercent(50);
                                setting.setLevelPercent(50);
                                setting.save();
                                try {
                                    OkHttpClient client1 = new OkHttpClient();
                                    //发送数据
                                    RequestBody requestBody1 = new FormBody.Builder().
                                            add("phone", personalMessageStore.getPhoneNumber()).build();
                                    Request request1 = new Request.Builder().url("http://39.96.41.6:8080/getUserInfo").post(requestBody1)
                                            .build();
                                    //获取数据
                                    Response response1 = client1.newCall(request1).execute();
                                    String responseData1=response1.body().string();
                                    JSONArray jsonArray1=new JSONArray(responseData1);
                                    JSONObject jsonObject1=jsonArray1.getJSONObject(0);
                                    String name, sex, idType, idNumber, birthday, province, city, district;
                                    name = jsonObject1.getString("name");
                                    sex = jsonObject1.getString("sex");
                                    idType = jsonObject1.getString("idType");
                                    idNumber = jsonObject1.getString("idNumber");
                                    birthday = jsonObject1.getString("birthday");
                                    province = jsonObject1.getString("province");
                                    city = jsonObject1.getString("city");
                                    district = jsonObject1.getString("district");
                                    personalMessageStore.setUserName(name);
                                    personalMessageStore.setUserSex(sex);
                                    personalMessageStore.setIdType(idType);
                                    personalMessageStore.setIdNumber(idNumber);
                                    personalMessageStore.setUserbirthDay(birthday);
                                    personalMessageStore.setProvince(province);
                                    personalMessageStore.setCity(city);
                                    personalMessageStore.setDistrict(district);
                                    personalMessageStore.save();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(rememberPass.isChecked()) {
                                            editor.putString("account", account);
                                            editor.putString("password", password);
                                            editor.putBoolean("remember_password", true);
                                        } else {
                                            editor.clear();
                                        }
                                        editor.apply();
                                    }
                                });
                                dialog.dismiss();
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
        //返回
        back = (ImageView) findViewById(R.id.login_titleBar_iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Regis = (TextView) findViewById(R.id.login_titleBar_tv_register);
        Regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
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

    public void showMyDialog(View v) {  //松开后就调用此方法开始转圈圈
        dialog =new MyProgressDialog(this, "登录中",R.drawable.loading);
        dialog.show();
        Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 20000);   //留一些加载时间
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
