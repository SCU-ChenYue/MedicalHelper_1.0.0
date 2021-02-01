package com.example.medicalhlepers.BasicActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.DoctorMessage.DoctorList;
import com.example.medicalhlepers.Guahao.DepartmentList;
import com.example.medicalhlepers.MapUse.MapUse;
import com.example.medicalhlepers.database.Hospital;
import com.example.medicalhlepers.database.MyDatabaseHelper1;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HospitalMessage extends AppCompatActivity {
    TextView textHosName;
    TextView textAddress;
    TextView textLevel;
    TextView textType;
    TextView textView14;
    ImageView locationView;
    ImageView callView;
    LinearLayout chooselocation;
    LinearLayout getChooselocation;
    RelativeLayout departmentList;
    LinearLayout tesekeshi;
    LinearLayout doctorListShow;
    RelativeLayout priceInquiry;
    ImageView getBack;
    public final static int SUCCESS = 0;
    String hosName;
    String hosPhone, hosAddre, hosLevel, hosType;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    textHosName.setText(hosName);
                    textAddress.setText(hosAddre);
                    textLevel.setText(hosLevel);
                    textType.setText(hosType);
                    textView14.setText(hosAddre);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_message);
        Intent intent = getIntent();
        hosName = intent.getStringExtra("hosName");
        textHosName = (TextView) findViewById(R.id.textView11);
        textAddress = (TextView) findViewById(R.id.tv_address);
        textLevel = (TextView) findViewById(R.id.textView10);
        textType = (TextView) findViewById(R.id.textView12);
        textView14 = (TextView) findViewById(R.id.textView14);
        initHosInfor();
        locationView = (ImageView) findViewById(R.id.location);
        locationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalMessage.this, MapUse.class);
                intent.putExtra("hosName", hosName);
                startActivity(intent);
            }
        });

        chooselocation = (LinearLayout) findViewById(R.id.chooselocation);
        chooselocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalMessage.this, MapUse.class);
                intent.putExtra("hosName", hosName);
                startActivity(intent);
            }
        });

        getChooselocation = (LinearLayout) findViewById(R.id.tab_yiyuandaohang);
        getChooselocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalMessage.this, MapUse.class);
                intent.putExtra("hosName", hosName);
                startActivity(intent);
            }
        });
        //拨打医院电话
        callView = (ImageView) findViewById(R.id.img_dial);
        callView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(HospitalMessage.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HospitalMessage.this, new
                            String[] {Manifest.permission.CALL_PHONE }, 1);
                } else {
                    call(hosPhone);
                }
            }
        });

        //跳转到科室列表界面
        departmentList = (RelativeLayout) findViewById(R.id.tab_yuyueguahao);
        departmentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalMessage.this, DepartmentList.class);
                intent.putExtra("hosName", hosName);
                startActivity(intent);
            }
        });

        tesekeshi = (LinearLayout) findViewById(R.id.tab_tesekeshi);
        tesekeshi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalMessage.this, DepartmentList.class);
                intent.putExtra("hosName", hosName);
                startActivity(intent);
            }
        });
        //跳转到专家列表
        doctorListShow = (LinearLayout) findViewById(R.id.tab_zhuanjiajieshao);
        doctorListShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalMessage.this, DoctorList.class);
                intent.putExtra("hosName", hosName);
                intent.putExtra("department", "");
                startActivity(intent);
            }
        });
        //价格查询
        priceInquiry = (RelativeLayout) findViewById(R.id.tab_jiagechaxun);
        priceInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //返回
        getBack = (ImageView) findViewById(R.id.img_back);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void call(String hosPhone) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+hosPhone));
            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call(hosPhone);
                } else {
                    Toast.makeText(this, "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void initHosInfor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("hosName", hosName).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/hosQuery").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    hosAddre = jsonObject.getString("hosAddre");
                    hosLevel = jsonObject.getString("hosLevel");
                    hosType = jsonObject.getString("hosType");
                    hosPhone = jsonObject.getString("hosPhone");
                    Message message = new Message();
                    message.what = SUCCESS;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}