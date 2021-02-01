package com.example.medicalhlepers.PersonalInformation;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.medicalhelpers.R;
import org.litepal.crud.DataSupport;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeIdNumber extends AppCompatActivity {
    EditText idNumberInput;
    Button saveIdNumber;
    ImageView imageView;
    List<PersonalMessageStore> list = DataSupport.findAll(PersonalMessageStore.class);
    PersonalMessageStore personalMessageStore = list.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_id_number);
        idNumberInput = (EditText) findViewById(R.id.name_input);

        idNumberInput.setText(list.get(0).getIdNumber());
        saveIdNumber = (Button) findViewById(R.id.tv_right);
        saveIdNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userIdNumber = idNumberInput.getText().toString();
                PersonalMessageStore personalMessageStore1 = new PersonalMessageStore();
                personalMessageStore1.setIdNumber(userIdNumber);
                personalMessageStore1.updateAll();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            //发送数据
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("phone", personalMessageStore.getPhoneNumber())
                                    .add("name","")
                                    .add("passwd", "")
                                    .add("sex", "")
                                    .add("idType", "")
                                    .add("idNumber", userIdNumber)
                                    .add("birthday", "").build();
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
