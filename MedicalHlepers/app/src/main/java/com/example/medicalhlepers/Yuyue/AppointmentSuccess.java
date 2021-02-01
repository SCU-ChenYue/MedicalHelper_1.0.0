package com.example.medicalhlepers.Yuyue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalhelpers.R;
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

public class AppointmentSuccess extends AppCompatActivity {
    TextView hospitalText;
    TextView departmentText;
    TextView dateText;
    TextView shangwuText;
    TextView docTitleText;
    TextView docNameText;
    TextView patientNameText;
    TextView patientSexText;
    Button backNumber;
    Button recordDetail;
    private List<PersonalMessageStore> personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStores.get(0);
    private String date, hospital, department, shangwu, doctor, title;  //挂号信息
    public final static int BTN_BACK = 1;
    private Handler handler = new Handler() {
      public void handleMessage(Message msg) {
          switch (msg.what) {
              case BTN_BACK:
                  Toast.makeText(AppointmentSuccess.this, "成功退订！",
                          Toast.LENGTH_SHORT).show();
                  finish();
                  break;
              default:
                  break;
          }
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_success);
        final Intent intent = getIntent();
        date = intent.getStringExtra("date");
        hospital = intent.getStringExtra("hospital");
        department = intent.getStringExtra("department");
        shangwu = intent.getStringExtra("shangwu");
        doctor = intent.getStringExtra("doctor");
        title = intent.getStringExtra("title");

        hospitalText = (TextView) findViewById(R.id.tv_hospital);
        hospitalText.setText(hospital);
        departmentText = (TextView) findViewById(R.id.tv_hospital_department);
        departmentText.setText(department);
        dateText = (TextView) findViewById(R.id.tv_date);
        dateText.setText(date);
        shangwuText = (TextView) findViewById(R.id.tv_shangwu);
        shangwuText.setText(shangwu);
        docTitleText = (TextView) findViewById(R.id.tv_doc_title);
        docTitleText.setText(title);
        docNameText = (TextView) findViewById(R.id.tv_doc_name);
        docNameText.setText(doctor);
        patientNameText = (TextView) findViewById(R.id.tv_name);
        patientNameText.setText(personalMessageStore.getUserName());
        patientSexText = (TextView) findViewById(R.id.tv_sex);
        patientSexText.setText(personalMessageStore.getUserSex());
        backNumber = (Button) findViewById(R.id.btn_back_num);
        recordDetail = (Button) findViewById(R.id.btn_deal_detail);
        recordDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppointmentSuccess.this, AppointmentMenu.class);
                intent.putExtra("date", date);
                intent.putExtra("hospital", hospital);
                intent.putExtra("department", department);
                intent.putExtra("shangwu", shangwu);
                intent.putExtra("doctor", doctor);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });
        backNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            //发送数据
                            RequestBody requestBody = new FormBody.Builder().
                                    add("phone", personalMessageStore.getPhoneNumber()).
                                    add("shangwu", shangwu).
                                    add("doctor", doctor).
                                    add("date", date).build();
                            Request request = new Request.Builder().url("http://39.96.41.6:8080/ghDelete").post(requestBody)
                                    .build();
                            //获取数据
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseData);
                            String result = jsonArray.getString(0);
                            Log.d("Login", "删除成功");
                            Message message = new Message();
                            message.what = BTN_BACK;
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
