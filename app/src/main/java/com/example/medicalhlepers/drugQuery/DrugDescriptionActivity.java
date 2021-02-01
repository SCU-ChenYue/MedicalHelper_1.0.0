package com.example.medicalhlepers.drugQuery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalhelpers.R;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DrugDescriptionActivity extends AppCompatActivity {
    private String webUrl="http://39.96.41.6:8080/";
    private String localUrl="http://192.168.0.107:8080/";
    private String phone="18382122682";
    private TextView chufangText;
    private TextView componentText;
    private TextView guigeText;
    private TextView shiyingzheng;
    private Double price_all;
    private TextView Text_name;
    private Button buyDrug;
    private TextView jingjizheng;
    private String name_all;
    private String imageUrl_all;
    private TextView Text_price;
    private TextView buliangfanying;
    private TextView yongfaliang;
    private ImageView getBack;
    private ImageView share;
    private String DrugName;
    private String description; //描述
    private String imageUrl;
    private String drugType;    //西药中药
    private String des;         //适用症状
    private String specs;   //规格
    private static final String alihttp="http://39.96.41.6:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drug_descriptions);
        Toast.makeText(this, "进入药品界面", Toast.LENGTH_SHORT).show();
        System.out.println("======="+DrugName+imageUrl+price_all+drugType+specs+des);
        Intent intent = getIntent();
        DrugName = intent.getStringExtra("name");
        imageUrl=intent.getStringExtra("imageUrl");
        price_all=intent.getDoubleExtra("price",0.00);
        name_all=DrugName;
        imageUrl_all=imageUrl;

        Text_name=(TextView)findViewById(R.id.drug_description_name);
        buyDrug=(Button)findViewById(R.id.drug_search_item_buy);
        Text_price=(TextView)findViewById(R.id.drug_description_price2);
        chufangText = (TextView) findViewById(R.id.detail_drug2);
        componentText = (TextView) findViewById(R.id.detail_drug3);
        guigeText = (TextView) findViewById(R.id.drugnum);
        jingjizheng = (TextView) findViewById(R.id.detail_merchant_tv_hours);
        shiyingzheng = (TextView) findViewById(R.id.detail_merchant_tv_address);
        buliangfanying = (TextView) findViewById(R.id.detail_merchant_ungoodb);
        yongfaliang = (TextView) findViewById(R.id.detail_merchant_usemethod);
        getBack = (ImageView) findViewById(R.id.detail_title_iv_back);
        share = (ImageView) findViewById(R.id.detail_title_iv_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DrugDescriptionActivity.this, "分享失败",
                        Toast.LENGTH_SHORT).show();
            }
        });
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initDrugInfor();
        buyDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.drug_search_item_buy:
                        addItemToDataBase(phone,name_all,price_all);
                        Toast.makeText(DrugDescriptionActivity.this, "加入购物车！",
                                Toast.LENGTH_SHORT).show();
                }
            }
        });
        buyDrug.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }
    public void addItemToDataBase(final String phone,final String itemName,final Double price){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("phone",phone)
                            .add("itemName",itemName).add("price",price.toString()).build();
                    Request request = new Request.Builder().url(webUrl+"addItem").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initDrugInfor() {  //获取药品的完整信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("kd", DrugName)
                            .add("num",""+0).build();
                    Request request = new Request.Builder().url(alihttp+"getDrugList").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray = new JSONArray(responseData);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    DrugName = jsonObject.getString("drugName");
                    imageUrl = jsonObject.getString("imageUrl");
                    price_all = jsonObject.getDouble("price");
                    drugType = jsonObject.getString("type");
                    specs = jsonObject.getString("specification");
                    des = jsonObject.getString("description");  //适应症，禁忌症，不良反应，用法用量，成分
                    Log.d("TAG", DrugName);
                    System.out.println("======="+DrugName+imageUrl+price_all+drugType+specs+des);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showUi();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showUi() {
        Text_name.setText(DrugName);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        String prices = nf.format(price_all);
        Text_price.setText(prices);
        Uri uri = Uri.parse(imageUrl);
        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
        draweeView.setImageURI(uri);
        guigeText.setText(specs);
        chufangText.setText("处方类型："+drugType);

        //des需要处理 des里面有适应症，禁忌症，不良反应，用法用量，成分
        String[] drugInfor = des.split("\\|");
        String shiying = drugInfor[0];
        String jingji = drugInfor[1];
        String buliang = drugInfor[2];
        String yongfa = drugInfor[3];
        String component = drugInfor[4];
        shiyingzheng.setText(shiying);
        jingjizheng.setText(jingji);
        buliangfanying.setText(buliang);
        yongfaliang.setText(yongfa);
        componentText.setText(component);
    }
}
