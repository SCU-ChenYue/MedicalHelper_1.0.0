package com.example.medicalhlepers.drugQuery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.medicalhelpers.R;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Drug_shop_item_adapter extends ArrayAdapter<Drug> {
    String webUrl="http://39.96.41.6:8080/";
    String localUrl="http://192.168.0.107:8080/";
    String phone="18382122682";
    private LocalBroadcastManager localBroadcastManager;
    private int resourceId;
    private ViewHolder convertViewHolder;
    public Drug_shop_item_adapter(Context context, int textViewResourceId, List<Drug> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        localBroadcastManager=LocalBroadcastManager.getInstance(this.getContext());
    }
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Drug drug=getItem(position);
            View view;
            final ViewHolder viewHolder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView)view.findViewById(R.id.drug_shop_item_delete);
                viewHolder.simpleDraweeView = (SimpleDraweeView)view.findViewById(R.id.drug_shop_item_draweeview);
                viewHolder.drugName = (TextView)view.findViewById(R.id.drug_shop_item_name);
                viewHolder.drugNum = (TextView)view.findViewById(R.id.drug_shop_item_num);
                viewHolder.drugPrice = (TextView)view.findViewById(R.id.drug_shop_item_price);
                convertViewHolder = viewHolder;//key
                view.setTag(viewHolder);
            }
            else
            {
                view =convertView;
                viewHolder=(ViewHolder) view.getTag();
                convertViewHolder=viewHolder;//key
            }
            //下载图片并显示
            try{
                getImageUrlFromHttp(drug.getName(),viewHolder);
            }catch (Exception e){
                e.printStackTrace();
            }
            //end
            //delete
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.drug_shop_item_delete:
                            final ProgressDialog pd = new ProgressDialog(getContext());
//设置标题
                            pd.setTitle("加载中");
//设置提示信息
                            pd.setMessage("网络请求中...");
//设置ProgressDialog 是否可以按返回键取消；
                            pd.setCancelable(false);
                            pd.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
//显示ProgressDialog
                            pd.show();
                            deleteItem(phone,drug.getName());
                            //delete item
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent=new Intent("com.example.medicalhelpers.freshshoplistview");
                                    localBroadcastManager.sendBroadcast(intent);
                                    pd.cancel();
                                }
                            };
                            Timer timer = new Timer();
                            timer.schedule(task, 500);//延时执行
                            //end
                    }
                }
            });
            //end
            viewHolder.imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_DOWN){
                        viewHolder.imageView.setImageResource(R.drawable.icon_leo_deleteselect);
                    }
                    else if(event.getAction()==MotionEvent.ACTION_UP){
                        viewHolder.imageView.setImageResource(R.drawable.icon_leo_delete);
                    }
                    return false;
                }
            });
            viewHolder.drugName.setText(drug.getName());
            String num=""+drug.getNum();
            viewHolder.drugNum.setText("×"+num);
            viewHolder.drugPrice.setText(drug.getPrice().toString());
            return view;
        }
        class ViewHolder{
            SimpleDraweeView simpleDraweeView;
            TextView drugName;
            TextView drugNum;
            ImageView imageView;
            TextView drugPrice;
        }
        public void deleteItem(final String phone,final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("phone", phone)
                            .add("itemName",name).build();
                    Request request = new Request.Builder().url(webUrl+"deleteItem").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        }
        public void getImageUrlFromHttp(final String name, final ViewHolder viewHolder){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("drug_name", name).build();
                    Request request = new Request.Builder().url(webUrl+"drugQueryItem").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    String result=jsonArray.getJSONObject(0).getString("result");
                    if(result.equals("fail"))
                        System.out.println("下载失败");
                    else
                        showInUI(result,viewHolder);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        }
        private void showInUI(final String imageUrl, final ViewHolder viewHolder){
        convertViewHolder.simpleDraweeView.post(new Runnable() {
            @Override
            public void run() {
                Uri uri=Uri.parse(imageUrl);
                viewHolder.simpleDraweeView.setImageURI(uri);
            }
        });
        }
    }
