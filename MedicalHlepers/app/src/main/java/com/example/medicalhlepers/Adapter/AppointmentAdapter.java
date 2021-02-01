package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Yuyue.Appointment;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {
    private int resourceId;
    public AppointmentAdapter(Context context, int textViewResourceId, List<Appointment> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Appointment appointment = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.appointmentDate = (TextView) view.findViewById(R.id.textView21);
            viewHolder.appointmentXingqi = (TextView) view.findViewById(R.id.text_Xingqi);
            viewHolder.appointmentHospital = (TextView) view.findViewById(R.id.textHospital);
            viewHolder.appointmentDepartment = (TextView) view.findViewById(R.id.textView24);
            viewHolder.appointmentShangwu = (TextView) view.findViewById(R.id.textShangwu);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.appointmentDepartment.setText(appointment.getApartment());
        viewHolder.appointmentHospital.setText(appointment.getHospital());
        viewHolder.appointmentXingqi.setText(appointment.getFlag());
        viewHolder.appointmentDate.setText(appointment.getDate());
        viewHolder.appointmentShangwu.setText(appointment.getShangwu());
        return view;
    }

    class ViewHolder {
        TextView appointmentDate;
        TextView appointmentXingqi;
        TextView appointmentHospital;
        TextView appointmentDepartment;
        TextView appointmentShangwu;
    }
}
