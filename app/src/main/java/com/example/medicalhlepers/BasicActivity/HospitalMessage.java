package com.example.medicalhlepers.BasicActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.DoctorMessage.DoctorList;
import com.example.medicalhlepers.Guahao.DepartmentList;
import com.example.medicalhlepers.MapUse.MapUse;
import com.example.medicalhlepers.PriceQuery.PriceQuery;
import com.example.medicalhlepers.ThreeMainFragment.FragmentMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HospitalMessage extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    private TextView textHosName;
    private TextView textAddress;
    private TextView textLevel;
    private TextView textType;
    private TextView textView14;
    private ImageView locationView;
    private ImageView callView;
    private LinearLayout chooselocation;
    private LinearLayout getChooselocation;
    private RelativeLayout departmentList;
    private LinearLayout tesekeshi;
    private RelativeLayout searchForPrice;
    private LinearLayout doctorListShow;
    private RelativeLayout priceInquiry;
    private LinearLayout ll_point;
    private LinearLayout getBack;
    private ViewPager vp;
    public final static int SUCCESS = 0;
    private TextView tv_desc;
    private int[] imageResIds;
    //存放图片资源id的数组
    private ArrayList<ImageView> imageViews;
    //存放图片的集合
    private String[] contentDescs;
    //图片内容描述
    private int lastPosition;
    private String hosName;
    private String hosPhone, hosAddre, hosLevel, hosType;
    private boolean isRunning = false;
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
        initViews();
        initData();
        initAdapter();
        hosName = intent.getStringExtra("hosName");
        Toast.makeText(HospitalMessage.this, hosName, Toast.LENGTH_SHORT).show();
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
                intent.putExtra("hosAddre", hosAddre);
                startActivity(intent);
            }
        });

        getChooselocation = (LinearLayout) findViewById(R.id.tab_yiyuandaohang);
        getChooselocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalMessage.this, MapUse.class);
                intent.putExtra("hosName", hosName);
                intent.putExtra("hosAddre", hosAddre);
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
        //返回
        getBack = (LinearLayout) findViewById(R.id.header_layout_lift);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //价格查询
        searchForPrice = (RelativeLayout) findViewById(R.id.tab_jiagechaxun);
        searchForPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HospitalMessage.this, PriceQuery.class);
                startActivity(intent1);
            }
        });

        new Thread(){
            @Override
            public void run() {
                isRunning = true;
                while(isRunning){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { //在子线程中开启子线程
                            // 往下翻一页（setCurrentItem方法用来设置ViewPager的当前页）
                            vp.setCurrentItem(vp.getCurrentItem()+1);
                        }
                    });
                }
            }
        }.start();
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

    private void initViews() {
        //初始化放小圆点的控件
        ll_point = (LinearLayout) findViewById(R.id.ll_point);
        //初始化ViewPager控件
        vp = (ViewPager) findViewById(R.id.vp);
        //设置ViewPager的滚动监听
        vp.setOnPageChangeListener(this);
        //显示图片描述信息的控件
        tv_desc = (TextView) findViewById(R.id.tv_desc);
    }

    /*
     初始化数据
    */
    private void initData() {
        //初始化填充ViewPager的图片资源
        imageResIds = new int[]{R.drawable.bg11, R.drawable.bg12, R.drawable.bg13};
        //图片的描述信息
        contentDescs = new String[]{
                "支持在线诊疗或取号实地就诊",
                "在线购买药品，可获取发票和送货上门",
                "在线获取检验报告，不要到医院便可查询"
        };
        //保存图片资源的集合
        imageViews = new ArrayList<>();
        ImageView imageView;
        View pointView;
        //循环遍历图片资源，然后保存到集合中
        for (int i = 0; i < imageResIds.length; i++){
            //添加图片到集合中
            imageView = new ImageView(this);
            imageView.setBackgroundResource(imageResIds[i]);
            imageViews.add(imageView);
            //加小白点，指示器（这里的小圆点定义在了drawable下的选择器中了，也可以用小图片代替）
            pointView = new View(this);
            pointView.setBackgroundResource(R.drawable.selectot_bg_point); //使用选择器设置背景
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(8, 8);
            if (i != 0){
                //如果不是第一个点，则设置点的左边距
                layoutParams.leftMargin = 10;
            }
            pointView.setEnabled(false); //默认都是暗色的
            ll_point.addView(pointView, layoutParams);
        }
    }

    /*
      初始化适配器
     */
    private void initAdapter() {
        ll_point.getChildAt(0).setEnabled(true); //初始化控件时，设置第一个小圆点为亮色
        tv_desc.setText(contentDescs[0]); //设置第一个图片对应的文字
        lastPosition = 0; //设置之前的位置为第一个
        vp.setAdapter(new MyPagerAdapter());
        //设置默认显示中间的某个位置（这样可以左右滑动），这个数只有在整数范围内，可以随便设置
        vp.setCurrentItem(5000000); //显示5000000这个位置的图片
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
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/hospitalInfo").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    hosAddre = jsonObject.getString("hosAddress");
                    hosLevel = jsonObject.getString("hosGrade");
                    hosType = jsonObject.getString("hosCategory");
                    hosPhone = jsonObject.getString("hosPhone");
                    Log.i("currentLocation", hosAddre+" "+hosLevel+" "+hosType);
                    Message message = new Message();
                    message.what = SUCCESS;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
//当前的位置可能很大，为了防止下标越界，对要显示的图片的总数进行取余
        int newPosition = position % 3;
        //设置描述信息
        tv_desc.setText(contentDescs[newPosition]);
        //设置小圆点为高亮或暗色
        ll_point.getChildAt(lastPosition).setEnabled(false);
        ll_point.getChildAt(newPosition).setEnabled(true);
        lastPosition = newPosition; //记录之前的点
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    /*
      自定义适配器，继承自PagerAdapter
     */
    class MyPagerAdapter extends PagerAdapter {
        //返回显示数据的总条数，为了实现无限循环，把返回的值设置为最大整数
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        //指定复用的判断逻辑，固定写法：view == object
        @Override
        public boolean isViewFromObject(View view, Object object) {
            //当创建新的条目，又反回来，判断view是否可以被复用(即是否存在)
            return view == object;
        }

        //返回要显示的条目内容
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //container  容器  相当于用来存放imageView
            //从集合中获得图片
            int newPosition = position % 3; //数组中总共有5张图片，超过数组长度时，取摸，防止下标越界
            ImageView imageView = imageViews.get(newPosition);
            //把图片添加到container中
            container.addView(imageView);
            //把图片返回给框架，用来缓存
            return imageView;
        }

        //销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //object:刚才创建的对象，即要销毁的对象
            container.removeView((View) object);
        }
    }
}