package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.database.Hospital;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

//适配器
public class HospitalAdapter extends ArrayAdapter<Hospital> {
    private int resourceId;
    public HospitalAdapter(Context context, int textViewResourceId,  //子控件的ID
                           List<Hospital> objects) {//数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Hospital hospital = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.hospitalName = (TextView) view.findViewById(R.id.hospital_name);
            viewHolder.hospitalLevel = (TextView) view.findViewById(R.id.hospital_level);
            viewHolder.hospitalType = (TextView) view.findViewById(R.id.hospital_type);
            //viewHolder.hospitalPhoto = (SimpleDraweeView) view.findViewById(R.id.hospital_image);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.hospitalName.setText(hospital.getName());
        viewHolder.hospitalLevel.setText(hospital.getHosLevel());
        viewHolder.hospitalType.setText(hospital.getHosType());
        //viewHolder.hospitalPhoto.setImageURI(Uri.parse(hospital.getImageId()));
        return view;
    }

    class ViewHolder {
        TextView hospitalName;
        TextView hospitalLevel;
        TextView hospitalType;
        //SimpleDraweeView hospitalPhoto;
    }
}
