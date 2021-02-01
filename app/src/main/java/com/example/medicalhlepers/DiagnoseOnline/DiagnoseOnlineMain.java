package com.example.medicalhlepers.DiagnoseOnline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.OnlineDocAdapter;
import com.example.medicalhlepers.ChatRoom.ChatRoom;
import com.example.medicalhlepers.Doctor.Doctor;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;
import com.example.medicalhlepers.database.Hospital;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiagnoseOnlineMain extends AppCompatActivity implements View.OnClickListener{
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private ListView listView;
    private List<Doctor> list = new ArrayList<>();
    private OnlineDocAdapter adapter;
    private List<PersonalMessageStore> personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStores.get(0);
    private String status;
    private LinearLayout searchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose_online_main);
        Intent intent = getIntent();

        button1 = (Button) findViewById(R.id.but1);
        button2 = (Button) findViewById(R.id.but2);
        button3 = (Button) findViewById(R.id.but3);
        button4 = (Button) findViewById(R.id.but4);
        button5 = (Button) findViewById(R.id.but5);
        button6 = (Button) findViewById(R.id.but6);
        button7 = (Button) findViewById(R.id.but7);
        list.clear();
        listView = (ListView) findViewById(R.id.list_view16);
        adapter = new OnlineDocAdapter(DiagnoseOnlineMain.this, R.layout.doctor_item2,
                list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Doctor doctor = list.get(position);
                //Intent intent = new Intent(DiagnoseOnlineMain.this, ChatRoom.class);
                Intent intent = new Intent(DiagnoseOnlineMain.this, OnlineDoc.class);
                intent.putExtra("userName", personalMessageStore.getUserName());
                intent.putExtra("duifang", doctor.getName());
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });
        searchLayout = (LinearLayout) findViewById(R.id.relativeLayout1);
        setStyle(button1);
        setStyle(button2);
        setStyle(button3);
        setStyle(button4);
        setStyle(button5);
        setStyle(button6);
        setStyle(button7);
        getDocInfo();
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiagnoseOnlineMain.this, SearchForOnlineDoc.class);
                startActivity(intent);
            }
        });

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
    }

    public static void setStyle(Button button) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(5, Color.parseColor("#1296DB")); // 边框粗细及颜色
        drawable.setColor(0xFFFFFF); // 边框内部颜色
        button.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.but1:
                Intent intent = new Intent(DiagnoseOnlineMain.this, SpecialDepartment.class);
                intent.putExtra("department", "乳腺外科专科门诊");
                intent.putExtra("status", status);
                startActivity(intent);
                break;
            case R.id.but2:
                Intent intent2 = new Intent(DiagnoseOnlineMain.this, SpecialDepartment.class);
                intent2.putExtra("department", "全科");
                intent2.putExtra("status", status);
                startActivity(intent2);
                break;
            case R.id.but3:
                Intent intent3 = new Intent(DiagnoseOnlineMain.this, SpecialDepartment.class);
                intent3.putExtra("department", "内分泌科门诊");
                intent3.putExtra("status", status);
                startActivity(intent3);
                break;
            case R.id.but4:
                Intent intent4 = new Intent(DiagnoseOnlineMain.this, SpecialDepartment.class);
                intent4.putExtra("department", "呼吸内科专科门诊");
                intent4.putExtra("status", status);
                startActivity(intent4);
                break;
            case R.id.but5:
                Intent intent5 = new Intent(DiagnoseOnlineMain.this, SpecialDepartment.class);
                intent5.putExtra("hospital", "四川大学华西医院");
                intent5.putExtra("status", status);
                startActivity(intent5);
                break;
            case R.id.but6:
                Intent intent6 = new Intent(DiagnoseOnlineMain.this, SpecialDepartment.class);
                intent6.putExtra("department", "耳鼻喉科");
                intent6.putExtra("status", status);
                startActivity(intent6);
                break;
            case R.id.but7:
                Intent intent7 = new Intent(DiagnoseOnlineMain.this, SpecialDepartment.class);
                intent7.putExtra("status", status);
                startActivity(intent7);
                break;
        }
    }

    private void getDocInfo() {    //获取所有医生信息
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
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
