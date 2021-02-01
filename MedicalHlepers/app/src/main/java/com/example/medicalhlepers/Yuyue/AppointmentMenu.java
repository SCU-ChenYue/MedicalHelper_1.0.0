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

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppointmentMenu extends AppCompatActivity {
    private TextView hospitalText;
    private TextView departmentText;
    private TextView dateText;
    private TextView shangwuText;
    private TextView hosTitleText;
    private TextView hosNameText;
    private TextView patientNameText;
    private TextView patientSexText;
    private TextView patientPhoneText;
    private TextView patientIdText;
    private Button submitButton;
    private String date, hospital, department, shangwu, doctor, title;  //挂号信息
    private List<PersonalMessageStore> personalMessageStores;
    private PersonalMessageStore personalMessageStore;
    private String patientName, patientSex, patientPhone, patientIdNumber;    //用户信息
    public static final int SUCCESS_YUYUE = 1;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_YUYUE:
                    Intent intent = new Intent(AppointmentMenu.this, AppointmentSuccess.class);
                    intent.putExtra("date", date);
                    intent.putExtra("hospital", hospital);
                    intent.putExtra("department", department);
                    intent.putExtra("shangwu", shangwu);
                    intent.putExtra("doctor", doctor);
                    intent.putExtra("title", title);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_menu);
        //获取上一活动数据
        final Intent intent = getIntent();
        date = intent.getStringExtra("date");
        hospital = intent.getStringExtra("hospital");
        department = intent.getStringExtra("department");
        shangwu = intent.getStringExtra("shangwu");
        doctor = intent.getStringExtra("doctor");
        title = intent.getStringExtra("title");
        //获取数据库中用户数据
        personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = personalMessageStores.get(0);
        patientName = personalMessageStore.getUserName();
        patientSex = personalMessageStore.getUserSex();
        patientPhone = personalMessageStore.getPhoneNumber();
        patientIdNumber = personalMessageStore.getIdNumber();
        //实例化控件
        hospitalText = (TextView) findViewById(R.id.tv_hospital);
        hospitalText.setText(hospital);
        departmentText = (TextView) findViewById(R.id.tv_department);
        departmentText.setText(department);
        dateText = (TextView) findViewById(R.id.tv_doc_date);
        dateText.setText(date);
        shangwuText = (TextView) findViewById(R.id.tv_doc_shangwu);
        shangwuText.setText(shangwu);
        hosTitleText = (TextView) findViewById(R.id.tv_outpatient_type);
        hosTitleText.setText(title);
        hosNameText = (TextView) findViewById(R.id.tv_outpatient_name);
        hosNameText.setText(doctor);
        patientNameText = (TextView) findViewById(R.id.tv_name);
        patientNameText.setText(patientName);
        patientSexText = (TextView) findViewById(R.id.tv_sex);
        patientSexText.setText(patientSex);
        patientPhoneText = (TextView) findViewById(R.id.tv_phone);
        patientPhoneText.setText(patientPhone);
        patientIdText = (TextView) findViewById(R.id.tv_idcard);
        patientIdText.setText(patientIdNumber);
        //点击挂号
        submitButton = (Button) findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //访问服务器发送需挂号的信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("date", date)  //挂号日期
                                    .add("hospital",hospital)
                                    .add("department", department)
                                    .add("shangwu", shangwu)
                                    .add("doctor", doctor)
                                    .add("title", title)
                                    .add("patientName", patientName)
                                    .add("phone", patientPhone).build();
                            Request request = new Request.Builder().url("http://39.96.41.6:8080/ghAdd").post(requestBody)
                                    .build();
                            //获取数据
                            Response response = client.newCall(request).execute();
                            String responseData=response.body().string();
                            JSONArray jsonArray=new JSONArray(responseData);
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            String result=jsonObject.getString("result");
                            if (result.equals("预约成功")){
                                Log.d("Login", "预约成功");
                                Message message = new Message();
                                message.what = SUCCESS_YUYUE;
                                handler.sendMessage(message);   //发送
                                finish();
                            }
                            else{
                                Log.d("Login", "预约失败");
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
