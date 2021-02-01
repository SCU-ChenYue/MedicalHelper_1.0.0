package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.AIdiagnose.SymptomDao;
import com.example.medicalhlepers.Doctor.Doctor;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class SymptomAdapter extends ArrayAdapter<SymptomDao> {
    private int resourceId;
    public SymptomAdapter(Context context, int textViewResourcedId,
                         List<SymptomDao> objects) {
        super(context, textViewResourcedId, objects);
        resourceId = textViewResourcedId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SymptomDao symptom = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.symptomName = (TextView) view.findViewById(R.id.create_case_test_text);
            viewHolder.chooseFlag = (TextView) view.findViewById(R.id.create_case_test_status_text);
            viewHolder.chooseImage = (ImageView) view.findViewById(R.id.chooseImage);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.symptomName.setText(symptom.getSymptom());
        boolean flag = symptom.isFlag();
        if(flag) {  //选中
            viewHolder.chooseFlag.setText("已选择");
            viewHolder.chooseImage.setImageResource(R.drawable.dui_selected);
        } else {
            viewHolder.chooseFlag.setText("未选择");
            viewHolder.chooseImage.setImageResource(R.drawable.dui_select);
        }

        return view;
    }

    class ViewHolder {
        TextView symptomName;
        TextView chooseFlag;
        ImageView chooseImage;
    }
}