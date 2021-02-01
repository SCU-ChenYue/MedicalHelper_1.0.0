package com.example.medicalhlepers.drugQuery;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.medicalhelpers.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Drug_adapter extends ArrayAdapter<Drug_item> {
    private int resourceId;
    private ViewHolder convertViewHolder;
    public Drug_adapter(Context context, int textViewResourceId, List<Drug_item> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Drug_item drug_item=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder =new ViewHolder();
            viewHolder.simpleDraweeView=(SimpleDraweeView)view.findViewById(R.id.drug_image);
            viewHolder.drugName=(TextView)view.findViewById(R.id.drug_name);
            convertViewHolder=viewHolder;//key
            view.setTag(viewHolder);
        }
        else
        {
            view =convertView;
            viewHolder=(ViewHolder) view.getTag();
            convertViewHolder=viewHolder;//key
        }
        try{
            Uri uri=Uri.parse(drug_item.getImageUrl());
            viewHolder.simpleDraweeView.setImageURI(uri);
        }catch (Exception e){
            e.printStackTrace();
        }
        viewHolder.drugName.setText(drug_item.getName());
        return view;
    }
    class ViewHolder{
        SimpleDraweeView simpleDraweeView;
        TextView drugName;
    }
    private void loadImage(final String imageUrl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap imageBM=getNetWorkBitmap(imageUrl);
                }catch (Exception e){
                    System.out.println("图片下载失败");
                }
            }
        }).start();
    }
    public static Bitmap getNetWorkBitmap(String urlString) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(urlString);
            // 使用HttpURLConnection打开连接
            HttpURLConnection urlConn = (HttpURLConnection) imgUrl
                    .openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            // 将得到的数据转化成InputStream
            InputStream is = urlConn.getInputStream();
            // 将InputStream转换成Bitmap
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            System.out.println("[getNetWorkBitmap->]MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[getNetWorkBitmap->]IOException");
            e.printStackTrace();
        }
        return bitmap;
    }
}
