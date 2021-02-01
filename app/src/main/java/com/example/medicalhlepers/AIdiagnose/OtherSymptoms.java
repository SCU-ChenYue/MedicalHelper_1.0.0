package com.example.medicalhlepers.AIdiagnose;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.SymptomAdapter;
import com.example.medicalhlepers.Dialog.MyProgressDialog;
import com.example.medicalhlepers.MapUse.AMapUtils;
import com.example.medicalhlepers.MapUse.LngLat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.example.medicalhlepers.Dialog.MyProgressDialog;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

public class OtherSymptoms extends AppCompatActivity {
    private MyProgressDialog dialog;
    private ListView listView;
    private Button submit;
    private Button cancel;
    private SymptomAdapter adapter;
    private List<SymptomDao> list = new ArrayList<>();
    private LinearLayout linearLayout;
    private List<Symptom> symptomList = new ArrayList<>();
    private double lat, lon;
    private static final String alihttp="http://101.201.151.125:8000/";
    private String disease, kd2;
    private int distance = 50;
    private int distancePercent = 50;
    private int levelPercent = 50;
    private String hospitalchooseType;
    private String hospitalchooseLevel;
    private String docchooseLevel;
    private List<Hospital_2> list2 = new ArrayList<>();
    private PersonalMessageStore personalMessageStore;
    private List<PersonalMessageStore> personalMessageStoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_symptoms);
        Intent intent = getIntent();
        symptomList = (ArrayList<Symptom>) getIntent().getSerializableExtra("hoslist");
        lat = intent.getDoubleExtra("lat", 0.0);
        lon = intent.getDoubleExtra("lon", 0.0);
        kd2 = intent.getStringExtra("kd");

        personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = personalMessageStoreList.get(0);

        SharedPreferences pref = getSharedPreferences("setting1", MODE_PRIVATE);
        hospitalchooseType = pref.getString("hospitalchooseType", "");
        hospitalchooseLevel = pref.getString("hospitalchooseLevel", "");
        docchooseLevel = pref.getString("docchooseLevel", "");
        distance = pref.getInt("distance", 100);
        distancePercent = pref.getInt("distancePercent", 50);
        levelPercent = pref.getInt("levelPercent", 50);
        Log.i("currentLocation", hospitalchooseLevel+distance+levelPercent);

        initSymptomData();
        listView = (ListView) findViewById(R.id.list_view10);
        submit = (Button) findViewById(R.id.create_case_btn_finish_sumbit);
        cancel = (Button) findViewById(R.id.create_case_btn_finish_save);
        linearLayout = (LinearLayout) findViewById(R.id.header_layout_lift);
        cancel.setOnClickListener(new View.OnClickListener() {  //清空所有选择
            @Override
            public void onClick(View v) {
                for(int i = 0; i < list.size(); i++) {
                    list.get(i).setFlag(false);
                }
                adapter.notifyDataSetChanged();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //提交选择的病症给服务器
                for(int i = 0; i < list.size(); i++) {
                    if(list.get(i).isFlag()) {
                        disease = list.get(i).getDisease();
                        Log.i("currentLocation", "disease:"+disease);
                        break;
                    }
                }
                //发送选择的症状给服务器
                showMyDialog(v);
                sendTheMessage(disease);
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new SymptomAdapter(this, R.layout.symptoms_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(list.get(position).isFlag()) {
                    list.get(position).setFlag(false);
                } else {
                    list.get(position).setFlag(true);
                }
                //每次只能选择一项症状
                for(int i = 0; i < list.size(); i++) {
                    if(i == position)
                        continue;
                    list.get(i).setFlag(false);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initSymptomData() {    //从服务器接收数据
        for(int i = 0; i < symptomList.size(); i++) {
            List<String> strings = symptomList.get(i).getSymptoms();
           for(int j = 0; j < strings.size(); j++) {
               SymptomDao symptomDao = new SymptomDao(strings.get(j), false);
               symptomDao.setDisease(symptomList.get(i).getDiseaseName());
               list.add(symptomDao);
           }
        }
    }

    public void showMyDialog(View v) {  //松开后就调用此方法开始转圈圈
        dialog =new MyProgressDialog(this, "正在识别中",R.drawable.loading);
        dialog.show();
        Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 20000);   //留一些加载时间
    }

    private void sendTheMessage(final String disease) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    int idd = 1;
                    if(hospitalchooseLevel.equals("")) {
                        idd = 13;
                    } else if(hospitalchooseLevel.equals("三级甲等")) {
                        idd = 1;
                    }

                    RequestBody requestBody = new FormBody.Builder()
                            .add("disease", disease)
                            .add("mode",""+3)
                            .add("longitude",""+lon)
                            .add("latitude",""+lat)
                            .add("distance", ""+distance)
                            .add("hospital", ""+idd)
                            .add("distancePercent", ""+distancePercent)
                            .add("levelPercent",""+levelPercent)
                            .add("category", hospitalchooseType)
                            .build();
                    Log.i("currentLocation", "发送的"+lon+" "+lat+" "+distance+" "+levelPercent+
                            " "+distancePercent+" "+hospitalchooseType+" "+hospitalchooseLevel);
                    Request request = new Request.Builder().url(alihttp+"getHospitals").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();     //获取服务器识别后得到的数据
                    Log.i("currentLocation", responseData);
                    JSONObject jsonObject2 = new JSONObject(responseData);
                    final String disease = jsonObject2.getString("disease");
                    boolean ifFind = jsonObject2.getBoolean("ifFind");
                    list2.clear();
                    if(!ifFind) {   //如果没找到
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(OtherSymptoms.this, "查询失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        JSONArray hosList = jsonObject2.getJSONArray("result");
                        for(int i = 0; i < hosList.length(); i++) { //获取数据后进入DiagnoseResultMain活动进行显示
                            JSONObject jsonObject = hosList.getJSONObject(i);
                            System.out.println(jsonObject);
                            Log.i("currentLocation", jsonObject.toString());
                            Hospital_2 hospital2 = new Hospital_2();
                            hospital2.setName(jsonObject.getString("name"));
                            hospital2.setGrade(jsonObject.getString("grade"));
                            hospital2.setCategory(jsonObject.getString("category"));
                            hospital2.setLongitude(jsonObject.getJSONObject("location").getDouble("longitude"));
                            hospital2.setLatitude(jsonObject.getJSONObject("location").getDouble("latitude"));
                            hospital2.setPhone(jsonObject.getString("phone"));
                            hospital2.setDepartmentCategory(jsonObject.getString("departmentCategory"));
                            hospital2.setDepartmentName(jsonObject.getString("departmentName"));
                            hospital2.setAddress(jsonObject.getString("address"));

                            LngLat start = new LngLat(lon, lat);
                            LngLat end = new LngLat(jsonObject.getJSONObject("location").getDouble("longitude"),
                                    jsonObject.getJSONObject("location").getDouble("latitude"));
                            Log.d("currentLocation", ""+ AMapUtils.calculateLineDistance(start, end));
                            Log.d("currentLocation", hospital2.getDepartmentName());
                            DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
                            double distance = AMapUtils.calculateLineDistance(start, end);

                            if(distance > 1000) {
                                hospital2.setDistance(df.format((double) distance / 1000)+"公里");
                            } else {
                                hospital2.setDistance(distance+"米");
                            }
                            list2.add(hospital2);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(OtherSymptoms.this, "查询成功",
                                        Toast.LENGTH_SHORT).show();
                                Log.i("currentLocation", "准备跳转");
                                Intent intent = new Intent(OtherSymptoms.this,
                                        DiagonseResultMain.class);
                                intent.putExtra("hoslist", (Serializable) list2);
                                intent.putExtra("kd", kd2);
                                intent.putExtra("lat", lat);
                                intent.putExtra("lon", lon);
                                intent.putExtra("disease", disease);
                                startActivity(intent);
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
