package com.example.medicalhlepers.DiagnoseOnline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.OnlineDocAdapter;
import com.example.medicalhlepers.Doctor.Doctor;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

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

public class SpecialDepartment extends AppCompatActivity {
    private ListView listView;
    private List<Doctor> list = new ArrayList<>();
    private OnlineDocAdapter adapter;
    private List<PersonalMessageStore> personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStores.get(0);
    private RelativeLayout searchLayout;
    private TextView titleText;
    private String department, hospital;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_department);
        initDocList();
        Intent intent = getIntent();
        department = intent.getStringExtra("department");
        hospital = intent.getStringExtra("hospital");
        status = intent.getStringExtra("status");

        listView = (ListView) findViewById(R.id.list_view7);
        adapter = new OnlineDocAdapter(SpecialDepartment.this, R.layout.doctor_item2,
                list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Doctor doctor = list.get(position);
                //Intent intent = new Intent(DiagnoseOnlineMain.this, ChatRoom.class);
                Intent intent = new Intent(SpecialDepartment.this, OnlineDoc.class);
                intent.putExtra("userName", personalMessageStore.getUserName());
                intent.putExtra("duifang", doctor.getName());
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });
        titleText = (TextView) findViewById(R.id.tv_title);
        if(department != null)
            titleText.setText(department);
        else if(hospital != null)
            titleText.setText(hospital);
        searchLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpecialDepartment.this, SearchForOnlineDoc.class);
                startActivity(intent);
            }
        });
    }

    private void initDocList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.clear();
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().build();
                    Request request1 = new Request.Builder().url("http://39.96.41.6:8080/doctorAll")
                            .post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request1).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    int j = jsonArray.length();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        Doctor doctor = new Doctor();
                        String name, hospital, department, title;
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        name = jsonObject.getString("name");
                        status = jsonObject.getString("status");
                        hospital = jsonObject.getString("hospital");
                        department = jsonObject.getString("department");
                        title = jsonObject.getString("title");
                        doctor.setName(name);
                        doctor.setTitle(title);
                        doctor.setStatus(status);
                        doctor.setHospital(hospital);
                        doctor.setDepartment(department);
                        list.add(doctor);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ourDocInfor();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void ourDocInfor() {
        for(int i = list.size() - 1; i >= 0; i--) {
            Doctor doctor = list.get(i);
            if(department != null && !doctor.getDepartment().contains(department))
                list.remove(i);
            else if(hospital != null && !doctor.getHospital().contains(hospital))
                list.remove(i);
        }
        adapter.notifyDataSetChanged();
    }
}
