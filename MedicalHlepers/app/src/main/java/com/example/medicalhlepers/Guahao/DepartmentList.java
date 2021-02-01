package com.example.medicalhlepers.Guahao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.DoctorMessage.DoctorList;
import com.example.medicalhlepers.DoctorMessage.DoctorMessage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DepartmentList extends AppCompatActivity {
    EditText inputDepartment;
    TextView searchButton;
    ListView listView;
    ImageView deleteInput;
    String hosName;
    private ArrayList<String> list;
    private String[] data = {"乳腺外科专科门诊", "全科门诊", "内分泌科门诊", "呼吸内科专科门诊",
            "妇科门诊", "心血管内科门诊", "普外科门诊", "泌尿外科门诊", "消化内科门诊", "神经内科门诊",
            "神经外科门诊", "耳鼻咽喉科门诊", "肝胆胰外科门诊", "肿瘤内科门诊", "肠胃外科门诊", "骨科门诊", "血液科门诊",
            "风湿免疫科门诊", "胸外科门诊" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);
        Intent intent = getIntent();
        hosName = intent.getStringExtra("hosName");
        inputDepartment = (EditText) findViewById(R.id.auto_edit);
        searchButton = (TextView) findViewById(R.id.searchbutton2);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = new ArrayList<>(Arrays.asList(data));
                Iterator<String> iterator = list.iterator();
                String input = inputDepartment.getText().toString();
                if(input != null) {
                    for(int i = list.size()-1; i >= 0; i--) {
                        if(list.get(i).contains(input)) {
                            continue;
                        } else {
                            list.remove(i);
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(DepartmentList.this,
                        android.R.layout.simple_list_item_1, list);
                listView = (ListView) findViewById(R.id.list_view2);
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
            }
        });

        deleteInput = (ImageView) findViewById(R.id.ivDeleteText);
        deleteInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDepartment.setText(null);
            }
        });
    }

}
