package com.example.medicalhlepers.DoctorMessage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.AppointmentAdapter;
import com.example.medicalhlepers.Yuyue.Appointment;
import com.example.medicalhlepers.Yuyue.AppointmentMenu;

import java.util.ArrayList;

public class DoctorMessage extends AppCompatActivity {
    TextView TextDoctorName;
    TextView TextDepartment;
    TextView TextTitle;
    TextView TextHospital;
    TextView TextIntroduction;
    ImageView getBack;
    ArrayList<String> doctorMessage;
    ArrayList<Appointment> appointments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_message);
        Intent intent = getIntent();
        doctorMessage = intent.getStringArrayListExtra("DoctorMessage");
        initAppointment();
        TextDoctorName = (TextView) findViewById(R.id.tv_doctor_name);
        TextDoctorName.setText(doctorMessage.get(0));
        TextDepartment = (TextView) findViewById(R.id.tv_department);
        TextDepartment.setText(doctorMessage.get(1));
        TextTitle = (TextView) findViewById(R.id.tv_titles);
        TextTitle.setText(doctorMessage.get(2));
        TextHospital = (TextView) findViewById(R.id.tv_hospital);
        TextHospital.setText(doctorMessage.get(3));
        TextIntroduction = (TextView) findViewById(R.id.jianjie);
        TextIntroduction.setText(doctorMessage.get(4));
        getBack = (ImageView) findViewById(R.id.img_back);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(DoctorMessage.this,
                R.layout.appointment_item, appointments);
        ListView listView = (ListView) findViewById(R.id.listView10);
        listView.setAdapter(appointmentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment appointment = appointments.get(position);
                String date = appointment.getDate();    //日期
                String hospital = appointment.getHospital();    //医院
                String department = appointment.getApartment();     //部门
                String shangwu = appointment.getShangwu();
                String flag = appointment.getFlag();    //星期几
                String doctor = doctorMessage.get(0);   //医生名字
                String title = doctorMessage.get(2);    //医生头衔
                Intent intent = new Intent(DoctorMessage.this, AppointmentMenu.class);
                intent.putExtra("date", date);
                intent.putExtra("hospital", hospital);
                intent.putExtra("department", department);
                intent.putExtra("flag", flag);
                intent.putExtra("doctor", doctor);
                intent.putExtra("title", title);
                intent.putExtra("shangwu", shangwu);
                startActivity(intent);
            }
        });

    }

    public void initAppointment() {
        Appointment appointment1 = new Appointment("2020-02-20", "星期四",
                doctorMessage.get(3), doctorMessage.get(1), "上午");
        appointments.add(appointment1);
        Appointment appointment2 = new Appointment("2020-02-21", "星期五",
                doctorMessage.get(3), doctorMessage.get(1), "上午");
        appointments.add(appointment2);
        Appointment appointment3 = new Appointment("2020-02-22", "星期六",
                doctorMessage.get(3), doctorMessage.get(1), "下午");
        appointments.add(appointment3);
        Appointment appointment4 = new Appointment("2020-02-23", "星期日",
                doctorMessage.get(3), doctorMessage.get(1), "上午");
        appointments.add(appointment4);
        Appointment appointment5 = new Appointment("2020-02-24", "星期一",
                doctorMessage.get(3), doctorMessage.get(1), "下午");
        appointments.add(appointment5);
        Appointment appointment6 = new Appointment("2020-02-25", "星期二",
                doctorMessage.get(3), doctorMessage.get(1), "下午");
        appointments.add(appointment6);
        Appointment appointment7 = new Appointment("2020-02-26", "星期三",
                doctorMessage.get(3), doctorMessage.get(1), "上午");
        appointments.add(appointment7);

    }
}
