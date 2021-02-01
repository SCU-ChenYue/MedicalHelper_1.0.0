package com.example.medicalhlepers.AIdiagnose;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class ChangeSetting extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView textView;
    private SeekBar seekBar2;
    private TextView juliText;
    private TextView levelText;
    private LinearLayout getBack;
    private RadioGroup hospitalType;
    private RadioButton morenHospital;
    private RadioButton zongheHospital;
    private RadioGroup hospitalLevel;
    private RadioButton morenLevel;
    private RadioButton jiadengLevel;
    private RadioGroup doctorLevel;
    private RadioButton morenDoc;
    private RadioButton zhurenDoc;
    private Button saveSetting;
    private int distance = 50;
    private int distancePercent = 50;
    private int levelPercent = 50;
    private String hospitalchooseType;
    private String hospitalchooseLevel;
    private String docchooseLevel;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_setting);
        //String: hospitalchooseType hospitalchooseLevel docchooseLevel
        //int: distance(距离：单位千米）
        //int: distancePercent, levelPercent 加起来是100
        SharedPreferences pref = getSharedPreferences("setting1", MODE_PRIVATE);
        hospitalchooseType = pref.getString("hospitalchooseType", "");
        hospitalchooseLevel = pref.getString("hospitalchooseLevel", "");
        docchooseLevel = pref.getString("docchooseLevel", "");
        distance = pref.getInt("distance", 100);
        distancePercent = pref.getInt("distancePercent", 50);
        levelPercent = pref.getInt("levelPercent", 50);

        getBack = (LinearLayout) findViewById(R.id.header_layout_lift);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        //seekbar设置监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*
             * seekbar改变时的事件监听处理
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
}
