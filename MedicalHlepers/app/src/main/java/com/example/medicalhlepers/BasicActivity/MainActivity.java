package com.example.medicalhlepers.BasicActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Doctor.Doctor;
import com.example.medicalhlepers.HospitalAddress.HosAddre;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;
import com.example.medicalhlepers.ThreeMainFragment.FragmentFind;
import com.example.medicalhlepers.ThreeMainFragment.FragmentHome;
import com.example.medicalhlepers.ThreeMainFragment.FragmentMessage;
import com.example.medicalhlepers.database.MyDatabaseHelper1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private MyDatabaseHelper1 dbHelper;
    private List<PersonalMessageStore> list;
    private PersonalMessageStore personalMessageStore;

    // 设置默认进来是tab 显示的页面
    private void setDefaultFragment(){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, new FragmentMessage());
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.replace(R.id.container,new FragmentHome());
                    transaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    transaction.replace(R.id.container,new FragmentFind());
                    transaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    transaction.replace(R.id.container,new FragmentMessage());
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //创建数据库
        dbHelper = new MyDatabaseHelper1(this, "hospitalList.db", null, 2);
        dbHelper.getWritableDatabase();  //检查是否存在该数据库
        initPersonalInfor();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setDefaultFragment();
    }

    private void initPersonalInfor() {  //在程序运行一开始，便获取个人信息
        list = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = list.get(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/getUserInfo").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                    String name, sex, idType, idNumber, birthday;
                    name = jsonObject.getString("name");
                    sex = jsonObject.getString("sex");
                    idType = jsonObject.getString("idType");
                    idNumber = jsonObject.getString("idNumber");
                    birthday = jsonObject.getString("birthday");
                    personalMessageStore.setUserName(name);
                    personalMessageStore.setUserSex(sex);
                    personalMessageStore.setIdType(idType);
                    personalMessageStore.setIdNumber(idNumber);
                    personalMessageStore.setUserbirthDay(birthday);
                    personalMessageStore.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void initLocation() {    //初始化坐标信息
        HosAddre hosAddre11 = new HosAddre();
        hosAddre11.setId(R.drawable.huaxiyiyuan);
        hosAddre11.setHosName("四川大学华西医院");
        hosAddre11.setLongitude(30.641514);
        hosAddre11.setLatitude(104.060469);
        hosAddre11.save();

        HosAddre hosAddre = new HosAddre();
        hosAddre.setId(R.drawable.huaxikouqiang);
        hosAddre.setHosName("四川大学华西口腔医院");
        hosAddre.setLongitude(30.642558);
        hosAddre.setLatitude(104.065517);
        hosAddre.save();

        HosAddre hosAddre1 = new HosAddre();
        hosAddre1.setId(R.drawable.huaxifunv);
        hosAddre1.setHosName("四川大学华西第二医院");
        hosAddre1.setLongitude(30.639336);
        hosAddre1.setLatitude(104.065713);
        hosAddre1.save();

        HosAddre hosAddre2 = new HosAddre();
        hosAddre2.setId(R.drawable.shengyiyuan);
        hosAddre2.setHosName("四川省人民医院");
        hosAddre2.setLongitude(30.6692427);
        hosAddre2.setLatitude(104.039916);
        hosAddre2.save();

        HosAddre hosAddre3 = new HosAddre();
        hosAddre3.setId(R.drawable.xieheyiyuan);
        hosAddre3.setHosName("北京协和医院");
        hosAddre3.setLongitude(39.912345);
        hosAddre3.setLatitude(116.415962);
        hosAddre3.save();

        HosAddre hosAddre4 = new HosAddre();
        hosAddre4.setId(R.drawable.zhongshanyiyuan);
        hosAddre4.setHosName("中山大学附属医院");
        hosAddre4.setLongitude(39.912345);
        hosAddre4.setLatitude(116.415962);
        hosAddre4.save();

        HosAddre hosAddre5 = new HosAddre();
        hosAddre5.setId(R.drawable.fudanyiyuan);
        hosAddre5.setHosName("复旦大学附属医院");
        hosAddre5.setLongitude(31.209039);
        hosAddre5.setLatitude(121.453363);
        hosAddre5.save();

        HosAddre hosAddre6 = new HosAddre();
        hosAddre6.setId(R.drawable.jfj);
        hosAddre6.setHosName("中国人民解放军总医院");
        hosAddre6.setLongitude(39.904549);
        hosAddre6.setLatitude(116.276591);
        hosAddre6.save();

        HosAddre hosAddre7 = new HosAddre();
        hosAddre7.setId(R.drawable.sddxqn);
        hosAddre7.setHosName("山东大学齐鲁医院");
        hosAddre7.setLongitude(36.656601);
        hosAddre7.setLatitude(117.018343);
        hosAddre7.save();

        HosAddre hosAddre8 = new HosAddre();
        hosAddre8.setId(R.drawable.znyy);
        hosAddre8.setHosName("中南大学湘雅二医院");
        hosAddre8.setLongitude(28.187932);
        hosAddre8.setLatitude(112.993977);
        hosAddre8.save();

        HosAddre hosAddre9 = new HosAddre();
        hosAddre9.setId(R.drawable.shjt);
        hosAddre9.setHosName("上海交通大学附属瑞金医院");
        hosAddre9.setLongitude(31.21175);
        hosAddre9.setLatitude(121.467641);
        hosAddre9.save();

        HosAddre hosAddre90 = new HosAddre();
        hosAddre90.setId(R.drawable.shjt);
        hosAddre90.setHosName("四川省第三人民医院");
        hosAddre90.setLongitude(30.668712);
        hosAddre90.setLatitude(104.062826);
        hosAddre90.save();
    }
}

