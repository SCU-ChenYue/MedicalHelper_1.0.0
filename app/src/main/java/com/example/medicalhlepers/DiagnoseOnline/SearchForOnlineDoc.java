package com.example.medicalhlepers.DiagnoseOnline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.OnlineDocAdapter;
import com.example.medicalhlepers.Doctor.Doctor;
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

public class SearchForOnlineDoc extends AppCompatActivity {
    private EditText inputDocName;
    private TextView searchDoc;
    private ListView listView;
    private List<Doctor> doctorList = new ArrayList<>();
    private String name, hospital, department, title, status;
    private OnlineDocAdapter adapter;
    private List<PersonalMessageStore> personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStores.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_online_doc);
        inputDocName = (EditText) findViewById(R.id.auto_edit);
        searchDoc = (TextView) findViewById(R.id.searchbutton);
        listView = (ListView) findViewById(R.id.list_view12);
        adapter = new OnlineDocAdapter(SearchForOnlineDoc.this, R.layout.doctor_item2,
                doctorList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Doctor doctor = doctorList.get(position);
                Intent intent = new Intent(SearchForOnlineDoc.this, OnlineDoc.class);
                intent.putExtra("userName", personalMessageStore.getUserName());
                intent.putExtra("duifang", doctor.getName());
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });
        searchDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputDocName.getText().toString();
                initDocResult(input);
            }
        });
    }

    private void initDocResult(final String input) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doctorList.clear();
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().add("name", input).build();
                    Request request1 = new Request.Builder().url("http://39.96.41.6:8080/doctorInfo")
                            .post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request1).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    for(int i = 0; i < jsonArray.length(); i++) {
                        Doctor doctor = new Doctor();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        hospital = jsonObject.getString("hospital");
                        department = jsonObject.getString("department");
                        title = jsonObject.getString("title");
                        name = jsonObject.getString("name");
                        doctor.setName(name);
                        doctor.setTitle(title);
                        doctor.setHospital(hospital);
                        doctor.setDepartment(department);
                        doctorList.add(doctor);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
