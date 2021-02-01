package com.example.medicalhlepers.BasicActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ChangePassword extends AppCompatActivity {
    EditText oldPasswordInput;
    EditText newPasswordInput1;
    EditText newPasswordInput2;
    Button savePassword;
    Button cancelButton;
    List<PersonalMessageStore> personalMessageStores;
    PersonalMessageStore personalMessageStore;

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

        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword1 = newPasswordInput1.getText().toString();
                String newPassword2 = newPasswordInput2.getText().toString();
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
                }
                if(!oldPassword.equals(personalMessageStore.getPassword())) {
                    Toast.makeText(ChangePassword.this, "密码输入错误",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else if(!newPassword1.equals(newPassword2)) {
                    Toast.makeText(ChangePassword.this, "请输入正确的新密码",
                            Toast.LENGTH_SHORT).show();
                    flag = 1;
                }
            }
        });
    }
}
