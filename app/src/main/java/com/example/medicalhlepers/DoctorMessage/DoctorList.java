package com.example.medicalhlepers.DoctorMessage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.DoctorAdapter;
import com.example.medicalhlepers.Doctor.Doctor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DoctorList extends AppCompatActivity {
    String hosName;
    String department = null;
    List<Doctor> doctorList = new ArrayList<>();
    public final static int SUCCESS_HOS = 0;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  SUCCESS_HOS:
                    initDoc();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        Intent intent = getIntent();
        hosName = intent.getStringExtra("hosName");
        department = intent.getStringExtra("department");
        if(department.equals("")) {
            initRecommendDoc();
        } else {
            doctorInfo();
        }
        initDoc();
    }

    private void initDoc() {
        DoctorAdapter adapter = new DoctorAdapter(DoctorList.this, R.layout.doctor_item,
                doctorList);
        ListView listView = (ListView) findViewById(R.id.list_view3);
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
                Intent intent = new Intent(DoctorList.this, DoctorMessage.class);
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
    }

    private void doctorInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doctorList.clear();
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("hosName", hosName).
                            add("department", department).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/getDoctors").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
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
                    Message message = new Message();
                    message.what = SUCCESS_HOS;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initRecommendDoc() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doctorList.clear();
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("hosName", hosName).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/getRecommendedDoctors").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
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
                    Message message = new Message();
                    message.what = SUCCESS_HOS;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
