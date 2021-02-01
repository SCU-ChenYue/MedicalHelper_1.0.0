package com.example.medicalhlepers.Guahao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.DoctorMessage.DoctorList;
import com.example.medicalhlepers.DoctorMessage.DoctorMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DepartmentList extends AppCompatActivity {
    private EditText inputDepartment;
    private TextView searchButton;
    private ListView listView;
    private ImageView deleteInput;
    private String hosName;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> tempList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private LinearLayout getBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);
        Intent intent = getIntent();
        hosName = intent.getStringExtra("hosName");
        inputDepartment = (EditText) findViewById(R.id.auto_edit);
        searchButton = (TextView) findViewById(R.id.searchbutton2);
        adapter = new ArrayAdapter<String>(DepartmentList.this,
                android.R.layout.simple_list_item_1, tempList);
        listView = (ListView) findViewById(R.id.list_view2);
        listView.setAdapter(adapter);
        initDepartmentList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String department = list.get(position);
                Intent intent = new Intent(DepartmentList.this, DoctorList.class);
                intent.putExtra("hosName", hosName);
                intent.putExtra("department", department);
                startActivity(intent);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempList.clear();
                tempList.addAll(list);  //将list中内容复制到tempList中
                String input = inputDepartment.getText().toString();
                if(input != null) {
                    for(int i = tempList.size()-1; i >= 0; i--) {
                        if(tempList.get(i).contains(input)) {
                            continue;
                        } else {
                            tempList.remove(i);
                        }
                    }
                }
                adapter = new ArrayAdapter<String>(DepartmentList.this,
                        android.R.layout.simple_list_item_1, tempList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String department = list.get(position);
                        Intent intent = new Intent(DepartmentList.this, DoctorList.class);
                        intent.putExtra("hosName", hosName);
                        intent.putExtra("department", department);
                        startActivity(intent);
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });
        deleteInput = (ImageView) findViewById(R.id.ivDeleteText);
        deleteInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDepartment.setText(null);
            }
        });
        getBack = (LinearLayout) findViewById(R.id.header_layout_lift);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void initDepartmentList() {     //从服务器获取对应医院的科室信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.clear();
                    tempList.clear();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().
                            add("hosName", hosName).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/getDepartments").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        list.add(jsonObject.getString("department"));
                        tempList.add(jsonObject.getString("department"));
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
