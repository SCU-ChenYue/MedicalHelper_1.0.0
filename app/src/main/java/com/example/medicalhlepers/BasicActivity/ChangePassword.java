package com.example.medicalhlepers.BasicActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Login.Register;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePassword extends AppCompatActivity {
    private EditText oldPasswordInput;
    private EditText newPasswordInput1;
    private EditText newPasswordInput2;
    private Button savePassword;
    private Button cancelButton;
    private List<PersonalMessageStore> personalMessageStores;
    private PersonalMessageStore personalMessageStore;
    private String newPassword1, newPassword2, oldPassword;
    public final static int FAIL_REGISTER = 0;
    public final static int SUCCESS_REGISTER = 1;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FAIL_REGISTER:
                    Toast.makeText(ChangePassword.this, "修改失败",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS_REGISTER:
                    Toast.makeText(ChangePassword.this, "修改成功",
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
        setContentView(R.layout.activity_change_password);
        personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = personalMessageStores.get(0);

        oldPasswordInput = (EditText) findViewById(R.id.ed4);
        newPasswordInput1 = (EditText) findViewById(R.id.ed5);
        newPasswordInput2 = (EditText) findViewById(R.id.ed6);
        savePassword = (Button) findViewById(R.id.btn_submit);
        cancelButton = (Button) findViewById(R.id.btn_del);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;
                oldPassword = oldPasswordInput.getText().toString();
                newPassword1 = newPasswordInput1.getText().toString();
                newPassword2 = newPasswordInput2.getText().toString();
                if(oldPassword.equals("")) {
                    Toast.makeText(ChangePassword.this, "旧密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else if(newPassword1.equals("") && flag == 0) {
                    Toast.makeText(ChangePassword.this, "新密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else if(newPassword2.equals("") && flag == 0) {
                    Toast.makeText(ChangePassword.this, "请再次输入新密码",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else if(!newPassword1.equals(newPassword2)) {
                    Toast.makeText(ChangePassword.this, "请输入正确的新密码",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else if(Register.checkForCorrect(newPassword1)) {
                    Toast.makeText(ChangePassword.this, "请输入6-15位数字或字母！",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                }
                if(flag == 0) {
                    initChangePass();
                }
            }
        });
    }

    private void initChangePass() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("oldPasswd", oldPassword).
                            add("newPasswd", newPassword1).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/modifyPasswd").
                            post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                    String result=jsonObject.getString("result");
                    if(result.equals("修改成功")) {
                        Log.d("Login", "注册成功");
                        Message message = new Message();
                        message.what = SUCCESS_REGISTER;
                        handler.sendMessage(message);
                    } else {
                        Log.d("Login", "已在");
                        Message message = new Message();
                        message.what = FAIL_REGISTER;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
