package com.example.medicalhlepers.drugQuery;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.medicalhelpers.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_drug_shop extends Fragment {
    private String webUrl="http://39.96.41.6:8080/";
    private String localUrl="http://192.168.0.107:8080/";
    private String phone="18382122682";
    private IntentFilter intentFilter;
    private FreshReceiver freshReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private View contentView;
    private List<Drug> DrugList=new ArrayList<Drug>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.drug_query_shop, container, false);
        getAllItemFromHttp(phone);
        localBroadcastManager=LocalBroadcastManager.getInstance(this.getActivity());
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.medicalhelpers.freshshoplistview");
        freshReceiver=new FreshReceiver();
        localBroadcastManager.registerReceiver(freshReceiver,intentFilter);

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
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
        //Fragment隐藏时调用
        }else {
        //Fragment显示时调用
            //test
            //
            getAllItemFromHttp(phone);
        }
    }
    public void getAllItemFromHttp(final String phone){
        final ProgressDialog pd = new ProgressDialog(getActivity());
//设置标题
        pd.setTitle("加载中");
//设置提示信息
        pd.setMessage("网络请求中...");
//设置ProgressDialog 是否可以按返回键取消；
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
//显示ProgressDialog
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("phone",phone)
                            .build();
                    Request request = new Request.Builder().url(webUrl+"queryItem").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    List<Drug> drugList=parseJSONWithJSONObject(responseData);
                    showInUI(drugList);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                pd.cancel();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000);//延时执行
    }

    public List<Drug> parseJSONWithJSONObject(String jsonData){
        List<Drug> tolist=new ArrayList<Drug>();
        try {
            JSONArray jsonArray=new JSONArray(jsonData);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String itemName=jsonObject.getString("itemName");
                int num=jsonObject.getInt("num");
                Double price=jsonObject.getDouble("price");
                tolist.add(new Drug(itemName,num,price));
            }
        }catch (Exception e){
            System.out.println("parseJSONWithJSONObject ERROR");
        }
        return tolist;
    }
    public void showInUI(final List<Drug> tolist){
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DrugList=tolist;
                Double totalPrice=new Double(0.0);
                for(Drug drug:DrugList){
                    totalPrice+=drug.getPrice()*drug.getNum();
                }
                Drug_shop_item_adapter adapter=new Drug_shop_item_adapter(getActivity(),
                        R.layout.drug_shops_item, tolist);
                ListView listView=(ListView) contentView.findViewById(R.id.drug_query_shop_listview);
                listView.setAdapter(adapter);
                TextView textView=(TextView) contentView.findViewById(R.id.drugPrice);
                textView.setText(totalPrice.toString()+"元");
            }
        });
    }
    class FreshReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            getAllItemFromHttp(phone);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(freshReceiver);
    }

}
