package com.example.medicalhlepers.BasicActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.database.Hospital;
import com.example.medicalhlepers.Adapter.HospitalAdapter;
import com.example.medicalhlepers.database.MyDatabaseHelper1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HospitalList extends AppCompatActivity {

    private List<Hospital> hospitalList = new ArrayList<>();
    private MyDatabaseHelper1 dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list);
        dbHelper = new MyDatabaseHelper1(this, "hospitalList.db", null, 2);

        Intent intent = getIntent();
        String hospitalName = intent.getStringExtra("hosName");
        initHospitals();
        if(hospitalName != null) {
            Iterator<Hospital> iterator = hospitalList.iterator();  //显示选择了的数据
            while(iterator.hasNext()) {
                if(iterator.next().getName().contains(hospitalName)) {
                    continue;
                }
                else {
                    iterator.remove();
                }
            }
        }

        HospitalAdapter adapter = new HospitalAdapter(HospitalList.this, R.layout.hospital_item,
                hospitalList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hospital hospital = hospitalList.get(position);
                String hospitalName = hospital.getName();
                Intent intent = new Intent(HospitalList.this, HospitalMessage.class);
                intent.putExtra("hosName", hospital.getName());
                startActivity(intent);
            }
        });
    }

    private void initHospitals() {
        //将数据库中的数据放入容器中
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Hospital", null, null, null,
                null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String hosName = cursor.getString(cursor.getColumnIndex("hosName"));
                String hosId = cursor.getString(cursor.getColumnIndex("hosId"));
                String hosWeb = cursor.getString(cursor.getColumnIndex("hosWeb"));
                String hosLevel = cursor.getString(cursor.getColumnIndex("hosLevel"));
                String hosType = cursor.getString(cursor.getColumnIndex("hosType"));
                String hosAddre = cursor.getString(cursor.getColumnIndex("hosAddre"));
                String hosPhone = cursor.getString(cursor.getColumnIndex("hosPhone"));
                String hosPhoto = cursor.getString(cursor.getColumnIndex("hosPhoto"));

                Hospital hospital = new Hospital(hosName, hosId, hosWeb, hosLevel, hosType, hosAddre,
                        hosPhone, hosPhoto);
                hospitalList.add(hospital);
            } while(cursor.moveToNext());
        }
        cursor.close();
    }
}
