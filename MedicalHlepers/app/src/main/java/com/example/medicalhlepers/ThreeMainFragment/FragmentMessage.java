package com.example.medicalhlepers.ThreeMainFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.HospitalAdapter;
import com.example.medicalhlepers.BasicActivity.HospitalList;
import com.example.medicalhlepers.BasicActivity.HospitalMessage;
import com.example.medicalhlepers.BasicActivity.SearchForHospital;
import com.example.medicalhlepers.Yuyue.AppointmentRecords;
import com.example.medicalhlepers.database.Hospital;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentMessage extends Fragment {
    private List<Hospital> list = new ArrayList<>();
    public final int SUCCESS = 0;
    private TextView editTextSearch;
    private LinearLayout findHos;
    private LinearLayout findDoc;
    private RelativeLayout searchHos;
    private LinearLayout yuyueRecord;
    private ListView listView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 初始化控件布局
        View contentView = inflater.inflate(R.layout.fragment_fragment_message, container, false);
        listView = (ListView) contentView.findViewById(R.id.list_view5);
        initHospitalInfor("");
        findHos = (LinearLayout) contentView.findViewById(R.id.findhos1);
        findHos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchForHospital.class);
                startActivity(intent);
            }
        });
        //查找医生
        findDoc = (LinearLayout) contentView.findViewById(R.id.finddoc);
        findDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //初始化litepal数据库
        //initHospitalAddress();

        //进入医院搜索界面
        searchHos = (RelativeLayout) contentView.findViewById(R.id.lll_search);
        searchHos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchForHospital.class);
                startActivity(intent);
            }
        });
        editTextSearch = (TextView) contentView.findViewById(R.id.search_hospital);
        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchForHospital.class);
                startActivity(intent);
            }
        });
        //预约记录查询
        yuyueRecord = (LinearLayout) contentView.findViewById(R.id.ll_yygh);
        yuyueRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AppointmentRecords.class);
                startActivity(intent);
            }
        });
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onOptionsMenuClosed(@NonNull Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    public void initHospitalAddress() {
        LitePal.getDatabase();
    }

    private void initHospitalInfor(final String hospitalName) {  //从服务器获取医院信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.clear();
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("hosName", hospitalName).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/hosQuery").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    int j = jsonArray.length();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Hospital hospital = new Hospital();
                        hospital.setName(jsonObject.getString("hosName"));
                        hospital.setHosPhoto1(jsonObject.getString("hosId"));
                        hospital.setHosWeb(jsonObject.getString("hosWeb"));
                        hospital.setHosLevel(jsonObject.getString("hosLevel"));
                        hospital.setHosType(jsonObject.getString("hosType"));
                        hospital.setHosAddre(jsonObject.getString("hosAddre"));
                        hospital.setHosPhone(jsonObject.getString("hosPhone"));
                        hospital.setHosPhoto2(jsonObject.getString("hosId"));
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

    private void initHosList() {
        HospitalAdapter adapter = new HospitalAdapter(getActivity(), R.layout.hospital_item,
                list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hospital hospital = list.get(position);
                Intent intent = new Intent(getActivity(), HospitalMessage.class);
                intent.putExtra("hosName", hospital.getName());
                startActivity(intent);
            }
        });
    }
}