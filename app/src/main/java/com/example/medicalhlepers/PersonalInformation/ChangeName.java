package com.example.medicalhlepers.PersonalInformation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import java.util.List;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeName extends AppCompatActivity {
    private EditText nameInput;
    private TextView saveName;
    private ImageView imageView;
    private List<PersonalMessageStore> list = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = list.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        nameInput = (EditText) findViewById(R.id.name_input);
        nameInput.setText(list.get(0).getUserName());
        saveName = (TextView) findViewById(R.id.tv_right);
        saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = nameInput.getText().toString();
                personalMessageStore.setUserName(userName);
                personalMessageStore.updateAll();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            //发送数据
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("phone", personalMessageStore.getPhoneNumber())
                                    .add("name",userName)
                                    .add("passwd", "")
                                    .add("sex", "")
                                    .add("idType", "")
                                    .add("idNumber", "")
                                    .add("birthday", "")
                                    .add("province", "")
                                    .add("city", "")
                                    .add("district", "").build();
                            Request request = new Request.Builder().url("http://39.96.41.6:8080/modifyUserInfo").post(requestBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                finish();
            }
        });
        imageView = (ImageView) findViewById(R.id.img_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
