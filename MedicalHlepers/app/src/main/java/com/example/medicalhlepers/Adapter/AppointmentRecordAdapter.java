package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Doctor.Doctor;
import com.example.medicalhlepers.Yuyue.AppointmentClass;

import java.util.List;

public class AppointmentRecordAdapter extends ArrayAdapter<AppointmentClass> {
    private int resourceId;
    public AppointmentRecordAdapter(Context context, int textViewResourcedId,
                         List<AppointmentClass> objects) {
        super(context, textViewResourcedId, objects);
        resourceId = textViewResourcedId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppointmentClass appointmentClass = getItem(position);
        View view;
        AppointmentRecordAdapter.ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new AppointmentRecordAdapter.ViewHolder();
            viewHolder.hospitalText = (TextView) view.findViewById(R.id.tv_hospital);
            viewHolder.doctorText = (TextView) view.findViewById(R.id.tv_doc_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.tv_date);
            viewHolder.shangwuText = (TextView) view.findViewById(R.id.tv_shangwu);
            viewHolder.departmentText = (TextView) view.findViewById(R.id.tv_hospital_department);
            viewHolder.titleText = (TextView) view.findViewById(R.id.tv_doc_title);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (AppointmentRecordAdapter.ViewHolder) view.getTag();
        }
        viewHolder.hospitalText.setText(appointmentClass.getHosName());
        viewHolder.doctorText.setText(appointmentClass.getDoctorName());
        viewHolder.dateText.setText(appointmentClass.getDate());
        viewHolder.shangwuText.setText(appointmentClass.getShangwu());
        viewHolder.departmentText.setText(appointmentClass.getDepartment());
        viewHolder.titleText.setText(appointmentClass.getTitle());
        return view;
    }

    class ViewHolder {
        TextView hospitalText;
        TextView doctorText;
        TextView dateText;
        TextView shangwuText;
        TextView departmentText;
        TextView titleText;
    }
}
