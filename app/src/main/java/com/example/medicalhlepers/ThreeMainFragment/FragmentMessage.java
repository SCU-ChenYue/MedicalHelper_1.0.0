package com.example.medicalhlepers.ThreeMainFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.HospitalAdapter;
import com.example.medicalhlepers.Adapter.MyApplication;
import com.example.medicalhlepers.BasicActivity.HospitalMessage;
import com.example.medicalhlepers.BasicActivity.SearchForHospital;
import com.example.medicalhlepers.ChooseCity.GetJsonDataUtil;
import com.example.medicalhlepers.ChooseCity.JsonBean;
import com.example.medicalhlepers.DiagnoseOnline.DiagnoseOnlineMain;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;
import com.example.medicalhlepers.Tools.DragFloatActionButton;
import com.example.medicalhlepers.Yuyue.AppointmentRecords;
import com.example.medicalhlepers.database.Hospital;
import com.example.medicalhlepers.drugQuery.Drug_main_activity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentMessage extends Fragment implements ViewPager.OnPageChangeListener{
    private ArrayList<JsonBean> options1Items = new ArrayList<>(); //省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();//区
    private List<Hospital> list = new ArrayList<>();
    private PersonalMessageStore personalMessageStore;
    private List<PersonalMessageStore> personalMessageStoreList;
    public final int SUCCESS = 0;
    private TextView editTextSearch;
    private LinearLayout findHos;
    private LinearLayout findDoc;
    private LinearLayout searchHos;
    private LinearLayout yuyueRecord;
    private LinearLayout choooseLocation;
    private TextView cityText;
    private ImageView findddoc;
    private ListView listView;
    private ImageView DrugQuery;
    private DragFloatActionButton dragFloatActionButton;
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
    private ViewPager vp;
    private LinearLayout ll_point;
    private TextView tv_desc;
    private int[] imageResIds;
    //存放图片资源id的数组
    private ArrayList<ImageView> imageViews;
    //存放图片的集合
    private String[] contentDescs;
    //图片内容描述
    private int lastPosition;
    private boolean isRunning = false;
    //viewpager是否在自动轮询
    private Activity activity;
    public Context getContext() {
        if(activity == null) {
            return MyApplication.getContext();
        }
        return activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 初始化控件布局
        View contentView = inflater.inflate(R.layout.fragment_fragment_message, container, false);
        initViews(contentView);
        //M--model数据
        initData();
        //C--control控制器(即适配器)
        initAdapter();
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() { //在子线程中开启子线程
                            // 往下翻一页（setCurrentItem方法用来设置ViewPager的当前页）
                            vp.setCurrentItem(vp.getCurrentItem()+1);
                        }
                    });
                }
            }
        }.start();
        //初始化地址选择器信息

        personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = personalMessageStoreList.get(0);

        initJsonData();
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
        //专家在线问诊
        findDoc = (LinearLayout) contentView.findViewById(R.id.finddoc);
        findDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiagnoseOnlineMain.class);
                startActivity(intent);
            }
        });
        findddoc = (ImageView) contentView.findViewById(R.id.findddoc);
        findddoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiagnoseOnlineMain.class);
                startActivity(intent);
            }
        });
        //初始化litepal数据库
        //initHospitalAddress();
        //drugQuery药瓶查询
        DrugQuery=(ImageView) contentView.findViewById(R.id.imageview_jbzz);
        DrugQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageview_jbzz:
                        Intent intent = new Intent(getActivity(), Drug_main_activity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        //end
        //进入医院搜索界面
        searchHos = (LinearLayout) contentView.findViewById(R.id.lll_search);
        searchHos.setOnClickListener(new View.OnClickListener() {
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
        //选择地址
        Log.d("TAG", personalMessageStore.getProvince()+personalMessageStore.getCity()
                +personalMessageStore.getDistrict());
        cityText = (TextView) contentView.findViewById(R.id.titleBar_city_name);
        if(personalMessageStore.getDistrict() != null) {
            cityText.setText(personalMessageStore.getDistrict());
        } else {
            cityText.setText("北京");
        }
        cityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerView();
            }
        });
        choooseLocation = (LinearLayout) contentView.findViewById(R.id.titleBar_location_lay);
        choooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerView();
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
                startActivity(intent); }
        });
    }
    private void initViews(View contentView) {
        //初始化放小圆点的控件
        ll_point = (LinearLayout) contentView.findViewById(R.id.ll_point);
        //初始化ViewPager控件
        vp = (ViewPager) contentView.findViewById(R.id.vp);
        //设置ViewPager的滚动监听
        vp.setOnPageChangeListener(this);
        //显示图片描述信息的控件
        tv_desc = (TextView) contentView.findViewById(R.id.tv_desc);
    }

    /*
      初始化数据
     */
    private void initData() {
        //初始化填充ViewPager的图片资源
        imageResIds = new int[]{R.drawable.bg6,
                R.drawable.bg1,R.drawable.bg2,R.drawable.bg3};
        //图片的描述信息
        contentDescs = new String[]{
                "支持在线诊疗或取号实地就诊",
                "与医生实时聊天，陈述你的病情",
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
            imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(imageResIds[i]);
            imageViews.add(imageView);
            //加小白点，指示器（这里的小圆点定义在了drawable下的选择器中了，也可以用小图片代替）
            pointView = new View(getActivity());
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
    //界面销毁时，停止viewpager的轮询
    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    /*
      自定义适配器，继承自PagerAdapter
     */
    class MyPagerAdapter extends PagerAdapter{
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
            int newPosition = position % 4; //数组中总共有5张图片，超过数组长度时，取摸，防止下标越界
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
    //--------------以下是设置ViewPager的滚动监听所需实现的方法--------
    //页面滑动
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    //新的页面被选中
    @Override
    public void onPageSelected(int position) {
        //当前的位置可能很大，为了防止下标越界，对要显示的图片的总数进行取余
        int newPosition = position % 4;
        //设置描述信息
        tv_desc.setText(contentDescs[newPosition]);
        //设置小圆点为高亮或暗色
        ll_point.getChildAt(lastPosition).setEnabled(false);
        ll_point.getChildAt(newPosition).setEnabled(true);
        lastPosition = newPosition; //记录之前的点
    }
    //页面滑动状态发生改变
    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void showPickerView() {// 弹出选择器（省市区三级联动）
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                /*mTvAddress.setText(options1Items.get(options1).getPickerViewText() + "  "
                        + options2Items.get(options1).get(options2) + "  "
                        + options3Items.get(options1).get(options2).get(options3));*/
                cityText.setText(options3Items.get(options1).get(options2).get(options3));
                personalMessageStore.setProvince(options1Items.get(options1).getPickerViewText().
                        replace(" ", ""));
                personalMessageStore.setCity(options2Items.get(options1).get(options2).
                        replace(" ", ""));
                personalMessageStore.setDistrict(options3Items.get(options1).get(options2).get(options3).
                        replace(" ", ""));
                personalMessageStore.save();
                Log.d("TAG", personalMessageStore.getProvince()+personalMessageStore.getCity()
                        +personalMessageStore.getDistrict());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            //发送数据
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("phone", personalMessageStore.getPhoneNumber())
                                    .add("name","")
                                    .add("passwd", "")
                                    .add("sex", "")
                                    .add("idType", "")
                                    .add("idNumber", "")
                                    .add("birthday", "")
                                    .add("province", personalMessageStore.getProvince())
                                    .add("city", personalMessageStore.getCity())
                                    .add("district", personalMessageStore.getDistrict()).build();
                            Request request = new Request.Builder().url("http://39.96.41.6:8080/modifyUserInfo").post(requestBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }
    private void initJsonData() {//解析数据 （省市区三级联动）
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(getContext(), "province.json");//获取assets目录下的json文件数据
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三级）
            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

}
