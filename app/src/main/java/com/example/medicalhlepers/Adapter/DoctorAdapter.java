package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Doctor.Doctor;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class DoctorAdapter extends ArrayAdapter<Doctor> {
    private int resourceId;
    public DoctorAdapter(Context context, int textViewResourcedId,
                         List<Doctor> objects) {
        super(context, textViewResourcedId, objects);
        resourceId = textViewResourcedId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Doctor doctor = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.doctorName = (TextView) view.findViewById(R.id.textView21);
            viewHolder.doctorTitle = (TextView) view.findViewById(R.id.textView22);
            viewHolder.doctorHospital = (TextView) view.findViewById(R.id.textView23);
            viewHolder.doctorDepartment = (TextView) view.findViewById(R.id.textView24);
            viewHolder.doctorImage = (SimpleDraweeView) view.findViewById(R.id.imageView8);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.doctorName.setText(doctor.getName());
        viewHolder.doctorTitle.setText(doctor.getTitle());
        viewHolder.doctorHospital.setText(doctor.getHospital());
        viewHolder.doctorDepartment.setText(doctor.getDepartment());
        viewHolder.doctorImage.setImageURI(Uri.parse(doctor.getImageUrl()));
        return view;
    }

    class ViewHolder {
        TextView doctorName;
        TextView doctorTitle;
        TextView doctorHospital;
        TextView doctorDepartment;
        SimpleDraweeView doctorImage;
    }
}
