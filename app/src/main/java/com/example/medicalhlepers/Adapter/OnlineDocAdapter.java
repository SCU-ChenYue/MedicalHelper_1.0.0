package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Doctor.Doctor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OnlineDocAdapter extends ArrayAdapter<Doctor> {
    private int resourceId;
    private String hospital, department, title;
    public OnlineDocAdapter(Context context, int textViewResourcedId,
                         List<Doctor> objects) {
        super(context, textViewResourcedId, objects);
        resourceId = textViewResourcedId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Doctor doctor = getItem(position);
        View view;
        OnlineDocAdapter.ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new OnlineDocAdapter.ViewHolder();
            viewHolder.doctorName = (TextView) view.findViewById(R.id.textView21);
            viewHolder.doctorTitle = (TextView) view.findViewById(R.id.textView22);
            viewHolder.doctorHospital = (TextView) view.findViewById(R.id.textView23);
            viewHolder.doctorDepartment = (TextView) view.findViewById(R.id.textView24);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (OnlineDocAdapter.ViewHolder) view.getTag();
        }
        viewHolder.doctorName.setText(doctor.getName());
        viewHolder.doctorTitle.setText(doctor.getTitle());
        viewHolder.doctorHospital.setText(doctor.getHospital());
        viewHolder.doctorDepartment.setText(doctor.getDepartment());
        return view;
    }

    class ViewHolder {
        TextView doctorName;
        TextView doctorTitle;
        TextView doctorHospital;
        TextView doctorDepartment;
    }

}
