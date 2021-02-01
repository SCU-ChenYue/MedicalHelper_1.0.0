package com.example.medicalhlepers.AIdiagnose;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.DiagnoseHosAdapter;
import java.util.ArrayList;
import java.util.List;

public class DiagonseResultMain extends AppCompatActivity {
    private ListView hoslistView;
    private LinearLayout getBack;
    private TextView textView;
    private List<DiagnoseResult> diagnoseResults;
    private DiagnoseHosAdapter hosAdapter;
    private List<Hospital_2> hospital2s;
    private String disease;
    private double lat; //用户的
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagonse_result_main);
        final Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0.00);
        lon = intent.getDoubleExtra("lon", 0.00);

        hospital2s = (ArrayList<Hospital_2>) getIntent().getSerializableExtra("hoslist");
        disease = intent.getStringExtra("disease");
        diagnoseResults = new ArrayList<>();
        hoslistView = (ListView) findViewById(R.id.case_select_symptom_accompany_listView);

        hosAdapter = new DiagnoseHosAdapter(this, R.layout.diagnose_hospital_item, hospital2s);
        hoslistView.setAdapter(hosAdapter);
        hoslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hospital_2 hospital2 = hospital2s.get(position);
                Intent intent = new Intent(DiagonseResultMain.this, HospitalNavigation.class);
                Bundle bundle = new Bundle();
                hospital2.setLat(lat);
                hospital2.setLon(lon);
                bundle.putSerializable("hospital", hospital2);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        getBack = (LinearLayout) findViewById(R.id.header_layout_lift);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textView = (TextView) findViewById(R.id.diagnoseResult);
        textView.setText(disease);
    }
}
