package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.AIdiagnose.HospitalResult;
import com.example.medicalhlepers.AIdiagnose.Hospital_2;
import com.example.medicalhlepers.database.Hospital;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class DiagnoseHosAdapter extends ArrayAdapter<Hospital_2> {
    private int resourceId;
    public DiagnoseHosAdapter(Context context, int textViewResourceId,  //子控件的ID
                           List<Hospital_2> objects) {//数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Hospital_2 hospital = getItem(position);
        View view;
        DiagnoseHosAdapter.ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new DiagnoseHosAdapter.ViewHolder();
            viewHolder.hospitalName = (TextView) view.findViewById(R.id.textView11);
            viewHolder.hospitalLevel = (TextView) view.findViewById(R.id.textView10);
            viewHolder.hospitalType = (TextView) view.findViewById(R.id.textView12);
            viewHolder.hospitalAddre = (TextView) view.findViewById(R.id.textView14);
            viewHolder.hospitalDistance = (TextView) view.findViewById(R.id.textView13);
            viewHolder.departmentName = (TextView) view.findViewById(R.id.departmentName);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (DiagnoseHosAdapter.ViewHolder) view.getTag();
        }
        viewHolder.hospitalName.setText(hospital.getName());
        viewHolder.hospitalLevel.setText(hospital.getGrade());
        viewHolder.hospitalType.setText(hospital.getCategory());
        viewHolder.hospitalAddre.setText(hospital.getAddress());
        viewHolder.hospitalDistance.setText(hospital.getDistance());
        viewHolder.departmentName.setText(hospital.getDepartmentName());
        return view;
    }

    class ViewHolder {
        TextView hospitalName;
        TextView hospitalLevel;
        TextView hospitalType;
        TextView hospitalAddre;
        TextView hospitalDistance;
        TextView departmentName;
    }
}
