package com.example.medicalhlepers.Yuyue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.AppointmentRecordAdapter;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

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

public class AppointmentRecords extends AppCompatActivity {
    List<AppointmentClass> list = new ArrayList<>();
    List<PersonalMessageStore> personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
    PersonalMessageStore personalMessageStore = personalMessageStores.get(0);
    public final static int OVER = 1;
    private Handler handler = new Handler() {
      public void handleMessage(Message msg) {
          switch (msg.what) {
              case OVER:
                  AppointmentRecordAdapter adapter = new AppointmentRecordAdapter(AppointmentRecords.this,
                          R.layout.appointment_list_item, list);
                  ListView listView = (ListView) findViewById(R.id.list_view1);
                  listView.setAdapter(adapter);
                  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                      @Override
                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                          AppointmentClass appointmentClass = list.get(position);
                          Intent intent = new Intent(AppointmentRecords.this, AppointmentSuccess.class);
                          intent.putExtra("date", appointmentClass.getDate());
                          intent.putExtra("hospital", appointmentClass.getHosName());
                          intent.putExtra("department", appointmentClass.getDepartment());
                          intent.putExtra("shangwu", appointmentClass.getShangwu());
                          intent.putExtra("doctor", appointmentClass.getDoctorName());
                          intent.putExtra("title", appointmentClass.getTitle());
                          startActivity(intent);
                      }
                  });
                  break;
              default:
                  break;
          }
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_records);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.clear();
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/ghQuery").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    int j = jsonArray.length();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        AppointmentClass appointmentClass = new AppointmentClass();
                        appointmentClass.setDate(jsonObject.getString("date"));
                        appointmentClass.setDepartment(jsonObject.getString("department"));
                        appointmentClass.setDoctorName(jsonObject.getString("doctor"));
                        appointmentClass.setHosName(jsonObject.getString("hospital"));
                        appointmentClass.setShangwu(jsonObject.getString("shangwu"));
                        appointmentClass.setTitle(jsonObject.getString("title"));
                        list.add(appointmentClass);
                    }
                    Message message = new Message();
                    message.what = OVER;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
