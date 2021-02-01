package com.example.medicalhlepers.BasicActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.HospitalAdapter;
import com.example.medicalhlepers.database.Hospital;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchForHospital extends AppCompatActivity implements TextWatcher {
    private SwipeRefreshLayout swipeRefresh;
    private HospitalAdapter adapter;
    private List<Hospital> list = new ArrayList<>();
    public final int SUCCESS = 0;
    private EditText inputHosName;
    private TextView searchButton;
    private ImageView deleteInput;
    private String hospitalName;
    private LinearLayout getBack;
    private Handler handler = new Handler() {
      public void handleMessage(Message msg) {
          switch (msg.what) {
              case SUCCESS:
                  initHosList();
                  break;
              default:
                  break;
          }
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_search_for_hospital);
        //dbHelper = new MyDatabaseHelper1(this, "hospitalList.db", null, 2);
        inputHosName = (EditText) findViewById(R.id.auto_edit);
        inputHosName.addTextChangedListener(this);
        searchButton = (TextView) findViewById(R.id.searchbutton);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
        initHospitalInfor("");
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospitalName = inputHosName.getText().toString();
                initHospitalInfor(hospitalName);
            }
        });
        deleteInput = (ImageView) findViewById(R.id.ivDeleteText);
        deleteInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputHosName.setText("");
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

    private void initHosList() {
        adapter = new HospitalAdapter(SearchForHospital.this, R.layout.hospital_item,
               list);
        ListView listView = (ListView) findViewById(R.id.list_view4);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hospital hospital = list.get(position);
                Intent intent = new Intent(SearchForHospital.this, HospitalMessage.class);
                intent.putExtra("hosName", hospital.getName());
                startActivity(intent);
            }
        });
    }

    private void initHospitalInfor(final String hospitalName) {  //从服务器获取医院信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("hosName", hospitalName).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/hospitalQuery").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    list.clear();
                    int j = jsonArray.length();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Hospital hospital = new Hospital();
                        hospital.setName(jsonObject.getString("hosName"));
                        hospital.setHosLevel(jsonObject.getString("hosGrade"));
                        hospital.setHosType(jsonObject.getString("hosCategory"));
                        hospital.setHosAddre(jsonObject.getString("hosAddress"));
                        hospital.setHosPhone(jsonObject.getString("hosPhone"));
                        list.add(hospital);
                    }
                    Message message = new Message();
                    message.what = SUCCESS;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initHosList();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(inputHosName.getText().toString().equals("")) {
            list.clear();
            adapter.notifyDataSetChanged();
        } else {
            hospitalName = inputHosName.getText().toString();
            initHospitalInfor(hospitalName);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}
