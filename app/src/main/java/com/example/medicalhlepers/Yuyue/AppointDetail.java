package com.example.medicalhlepers.Yuyue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AppointDetail extends AppCompatActivity {
    private String date, hospital, department, shangwu, doctor, title;  //挂号信息
    private String patientName, patientSex, patientPhone, patientIdNumber, patientBirthday;    //用户信息
    private List<PersonalMessageStore> personalMessageStores;
    private PersonalMessageStore personalMessageStore;
    private TextView hospitalText;
    private TextView departmentText;
    private TextView timeText;
    private TextView docNameText;
    private TextView pacNameText;
    private TextView pacSexText;
    private TextView pacPhoneText;
    private TextView pacIdText;
    private TextView deatText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoint_detail);
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
        patientBirthday = personalMessageStore.getUserbirthDay();

        hospitalText = findViewById(R.id.tv_hospital);
        departmentText = findViewById(R.id.tv_department);
        timeText = findViewById(R.id.tv_doc_date);
        docNameText = findViewById(R.id.tv_outpatient_type);
        pacNameText = findViewById(R.id.tv_registered_name);
        pacSexText = findViewById(R.id.tv_sex);
        pacPhoneText = findViewById(R.id.tv_phone);
        pacIdText = findViewById(R.id.tv_idcard);

        hospitalText.setText(hospital);
        departmentText.setText(department);
        timeText.setText(date+"  "+shangwu);
        docNameText.setText(title+" "+doctor);
        pacNameText.setText(patientName);
        pacSexText.setText(patientSex);
        pacPhoneText.setText(patientPhone);
        pacIdText.setText(patientIdNumber);
    }
}
