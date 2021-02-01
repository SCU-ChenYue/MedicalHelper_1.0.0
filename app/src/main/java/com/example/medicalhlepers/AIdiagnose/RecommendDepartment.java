package com.example.medicalhlepers.AIdiagnose;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.DoctorAdapter;
import com.example.medicalhlepers.Doctor.Doctor;
import com.example.medicalhlepers.DoctorMessage.DoctorList;
import com.example.medicalhlepers.DoctorMessage.DoctorMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecommendDepartment extends AppCompatActivity {
    private TextView departmentName;
    private TextView hospitalName;
    private TextView hospitalLevel;
    private TextView hospitalType;
    private TextView hospitalAddre;
    private TextView hospitalDistance;
    private ListView listView;
    private Hospital_2 hospital_2;
    private List<Doctor> doctorList = new ArrayList<>();
    private DoctorAdapter adapter;
    private LinearLayout getBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_department);
        Intent intent = getIntent();
        hospital_2 = (Hospital_2) intent.getSerializableExtra("hospital");
        departmentName = (TextView) findViewById(R.id.textView11);
        hospitalName = (TextView) findViewById(R.id.hospital);
        hospitalLevel = (TextView) findViewById(R.id.textView10);
        hospitalType = (TextView) findViewById(R.id.textView12);
        hospitalAddre = (TextView) findViewById(R.id.textView14);
        hospitalDistance = (TextView) findViewById(R.id.textView13);
        listView = (ListView) findViewById(R.id.list_view8);
        getBack = (LinearLayout) findViewById(R.id.header_layout_lift);

        departmentName.setText(hospital_2.getDepartmentName());
        hospitalName.setText(hospital_2.getName());
        hospitalLevel.setText(hospital_2.getGrade());
        hospitalType.setText(hospital_2.getCategory());
        hospitalAddre.setText(hospital_2.getAddress());
        hospitalDistance.setText(hospital_2.getDistance());
        adapter = new DoctorAdapter(RecommendDepartment.this, R.layout.doctor_item,
                doctorList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override   //设计点击事件
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Doctor doctor = doctorList.get(position);
                String name = doctor.getName();
                String department = doctor.getDepartment();
                String title = doctor.getTitle();
                String hospital = doctor.getHospital();
                String Introduction = doctor.getIntroduction();
                String imageUrl = doctor.getImageUrl();
                Intent intent = new Intent(RecommendDepartment.this, DoctorMessage.class);
                ArrayList<String> message = new ArrayList<>();
                message.add(name);
                message.add(department);
                message.add(title);
                message.add(hospital);
                message.add(Introduction);
                message.add(imageUrl);
                intent.putStringArrayListExtra("DoctorMessage", message);
                startActivity(intent);
            }
        });
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doctorInfo();
    }

    private void doctorInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("hosName", hospital_2.getName()).
                            add("department", hospital_2.getDepartmentName()).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/getDoctors").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    doctorList.clear();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Doctor doctor = new Doctor();
                        doctor.setHospital(jsonObject.getString("hosName"));
                        doctor.setDepartment(jsonObject.getString("departmentName"));
                        doctor.setName(jsonObject.getString("name"));
                        doctor.setTitle(jsonObject.getString("position"));
                        doctor.setIntroduction(jsonObject.getString("experience"));
                        doctor.setImageUrl(jsonObject.getString("imageUrl"));
                        doctorList.add(doctor);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
