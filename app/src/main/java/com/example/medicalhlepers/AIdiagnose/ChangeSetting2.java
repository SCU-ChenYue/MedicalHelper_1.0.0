package com.example.medicalhlepers.AIdiagnose;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.BasicActivity.MainActivity;
import com.example.medicalhlepers.MapUse.AMapUtils;
import com.example.medicalhlepers.MapUse.LngLat;
import com.example.medicalhlepers.RecordHelper.AudioRecordActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeSetting2 extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView textView;
    private SeekBar seekBar2;
    private TextView juliText;
    private TextView levelText;
    private RadioGroup hospitalType;
    private RadioButton morenHospital;
    private RadioButton zongheHospital;
    private RadioGroup hospitalLevel;
    private RadioButton morenLevel;
    private RadioButton jiadengLevel;
    private RadioGroup doctorLevel;
    private RadioButton morenDoc;
    private RadioButton zhurenDoc;
    private LinearLayout getBack;
    private Button saveSetting;
    private int distance = 50;
    private int distancePercent = 50;
    private int levelPercent = 50;
    private String hospitalchooseType;
    private String hospitalchooseLevel;
    private String docchooseLevel;
    private List<Hospital_2> list;
    private static final String alihttp="http://101.201.151.125:8000/";
    private double lat; //用户的
    private double lon;
    private String kd;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_setting2);
        list = new ArrayList<>();
        //String: hospitalchooseType hospitalchooseLevel docchooseLevel
        //int: distance(距离：单位千米）
        //int: distancePercent, levelPercent 加起来是100
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0.00);
        lon = intent.getDoubleExtra("lon", 0.00);
        kd = intent.getStringExtra("kd");
        SharedPreferences pref = getSharedPreferences("setting1", MODE_PRIVATE);
        hospitalchooseType = pref.getString("hospitalchooseType", "");
        hospitalchooseLevel = pref.getString("hospitalchooseLevel", "");
        docchooseLevel = pref.getString("docchooseLevel", "");
        distance = pref.getInt("distance", 100);
        distancePercent = pref.getInt("distancePercent", 50);
        levelPercent = pref.getInt("levelPercent", 50);

        saveSetting = (Button) findViewById(R.id.update_my_info_btn_submit);
        hospitalType = (RadioGroup) findViewById(R.id.hospitalType);
        hospitalLevel = (RadioGroup) findViewById(R.id.hospitalLevel);
        doctorLevel = (RadioGroup) findViewById(R.id.docLevel);

        morenHospital = (RadioButton) findViewById(R.id.moren_radio);
        zongheHospital = (RadioButton) findViewById(R.id.zongheyiyuan);
        morenLevel = (RadioButton) findViewById(R.id.morenquanxuan_radio);
        jiadengLevel = (RadioButton) findViewById(R.id.sanjijiadeng);
        morenDoc = (RadioButton) findViewById(R.id.update_my_info_glasses_primary);
        zhurenDoc = (RadioButton) findViewById(R.id.update_my_info_glasses_expert);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(distance);
        textView = (TextView) findViewById(R.id.sousuodistance);
        getBack = (LinearLayout) findViewById(R.id.header_layout_lift);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //seekbar设置监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*
             * seekbar改变时的事件监听处理            *
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(progress+"公里");
                distance = progress;
                Log.d("debug",String.valueOf(seekBar.getId()));
            }
            /*
             * 按住seekbar时的事件监听处理
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            /*
             * 放开seekbar时的时间监听处理
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        juliText = (TextView) findViewById(R.id.distancepercent);
        levelText = (TextView) findViewById(R.id.levelpercent);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*
             * seekbar改变时的事件监听处理            *
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                juliText.setText("距离:"+progress+"%");
                levelText.setText("等级:"+(100-progress)+"%");
                distancePercent = progress;
                levelPercent = 100 - distancePercent;
                Log.d("debug",String.valueOf(seekBar.getId()));
            }
            /*
             * 按住seekbar时的事件监听处理
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            /*
             * 放开seekbar时的时间监听处理
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //设置默认选中的按钮
        initView();

        saveSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(morenHospital.isChecked()) { //默认医院
                    hospitalchooseType = "";
                }
                if(zongheHospital.isChecked()) {    //综合医院
                    hospitalchooseType = "综合医院";
                }
                if(morenLevel.isChecked()) {    //默认等级
                    hospitalchooseLevel = "";
                }
                if(jiadengLevel.isChecked()) {  //三级甲等
                    hospitalchooseLevel = "三级甲等";
                }
                if(morenDoc.isChecked()) {      //默认医师
                    docchooseLevel = "";
                }
                if(zhurenDoc.isChecked()) {     //主任医师
                    docchooseLevel = "主任医师";
                }
                //String: hospitalchooseType hospitalchooseLevel docchooseLevel
                //int: distance(距离：单位千米）
                //int: distancePercent, levelPercent 加起来是100
                SharedPreferences.Editor editor  = getSharedPreferences("setting1", MODE_PRIVATE).edit();
                editor.putString("hospitalchooseType", hospitalchooseType);
                editor.putString("hospitalchooseLevel", hospitalchooseLevel);
                editor.putString("docchooseLevel", docchooseLevel);
                editor.putInt("distance", distance);
                editor.putInt("distancePercent", distancePercent);
                editor.putInt("levelPercent", levelPercent);
                editor.apply();
                Log.i("currentLocation", "设置为："+distance+" "+levelPercent+
                        " "+distancePercent+" "+hospitalchooseType+" "+hospitalchooseLevel);
                httpRequest(kd);
                finish();
            }
        });
    }

    private void initView() {
        Log.i("currentLocation", "初始化打印1："+distance+" "+levelPercent+
                " "+distancePercent+" "+hospitalchooseType+" "+hospitalchooseLevel);
        if(hospitalchooseType.equals("综合医院")) {
            hospitalType.check(R.id.zongheyiyuan);
        } else {
            hospitalType.check(R.id.moren_radio);
        }

        if(hospitalchooseLevel.equals("三级甲等")) {
            hospitalLevel.check(R.id.sanjijiadeng);
        } else {
            hospitalLevel.check(R.id.morenquanxuan_radio);
        }

        if(docchooseLevel.equals("主任医师")) {
            doctorLevel.check(R.id.update_my_info_glasses_expert);
        } else {
            doctorLevel.check(R.id.update_my_info_glasses_primary);
        }
        seekBar.setProgress(distance);
        seekBar2.setProgress(distancePercent);
        textView.setText(""+distance+"公里");
        juliText.setText("距离："+distancePercent+"%");
        levelText.setText("等级："+levelPercent+"%");
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences("setting1", MODE_PRIVATE);
        hospitalchooseType = pref.getString("hospitalchooseType", "");
        hospitalchooseLevel = pref.getString("hospitalchooseLevel", "");
        docchooseLevel = pref.getString("docchooseLevel", "");
        distance = pref.getInt("distance", 100);
        distancePercent = pref.getInt("distancePercent", 50);
        levelPercent = pref.getInt("levelPercent", 50);
        initView();
    }

    private void httpRequest(final String kd) { //提交输入结果给服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("hos", kd+lon+" "+lat+"==========");
                    OkHttpClient client = new OkHttpClient();
                    int idd = 1;
                    if(hospitalchooseLevel.equals("")) {
                        idd = 13;
                    } else if(hospitalchooseLevel.equals("三级甲等")) {
                        idd = 1;
                    }
                    RequestBody requestBody = new FormBody.Builder()
                            .add("kd", kd)
                            .add("mode",""+3)
                            .add("longitude",""+lon)
                            .add("latitude",""+lat)
                            .add("distance", ""+distance)
                            .add("hospital", ""+idd)
                            .add("distancePercent", ""+distancePercent)
                            .add("levelPercent",""+levelPercent)
                            .add("category", hospitalchooseType)
                            .build();
                    Request request = new Request.Builder().url(alihttp+"test").post(requestBody)
                            .build();
                    Log.i("currentLocation", lon+" "+lat+" "+distance+" "+levelPercent);
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();     //获取服务器识别后得到的数据
                    JSONArray hosList = new JSONArray(responseData);
                    list.clear();
                    for(int i = 0; i < hosList.length(); i++) { //获取数据后进入DiagnoseResultMain活动进行显示
                        JSONObject jsonObject = hosList.getJSONObject(i);
                        System.out.println(jsonObject);
                        Log.i("currentLocation", jsonObject.getString("name"));
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
                        list.add(hospital2);
                        LngLat start = new LngLat(lon, lat);
                        LngLat end = new LngLat(jsonObject.getJSONObject("location").getDouble("longitude"),
                                jsonObject.getJSONObject("location").getDouble("latitude"));
                        Log.d("currentLocation", ""+ AMapUtils.calculateLineDistance(start, end));
                        DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
                        double distance = AMapUtils.calculateLineDistance(start, end);
                        if(distance > 1000) {
                            hospital2.setDistance(df.format((double) distance / 1000)+"公里");
                        } else {
                            hospital2.setDistance(distance+"米");
                        }
                    }
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //这里不能直接修改UI，需要使用runOnUiThread
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if(!list.isEmpty()) {
                                        Intent intent = new Intent(ChangeSetting2.this,
                                                DiagonseResultMain.class);
                                        intent.putExtra("hoslist", (Serializable) list);
                                        intent.putExtra("kd", kd);
                                        intent.putExtra("lat", lat);
                                        intent.putExtra("lon", lon);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(ChangeSetting2.this, "空的",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
