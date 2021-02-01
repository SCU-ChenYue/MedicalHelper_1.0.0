package com.example.medicalhlepers.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.PriceQuery.Examination;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {
    private List<Examination> examinationList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView priceText;
        private TextView inforText;
        public ViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.textView34);
            priceText = (TextView) view.findViewById(R.id.textView27);
            inforText = (TextView) view.findViewById(R.id.textView35);
        }
    }
    public ExamAdapter(List<Examination> examinationList) {
        this.examinationList = examinationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_service_price, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Examination examination = examinationList.get(position);
        holder.nameText.setText(examination.getExamName());
        holder.priceText.setText(examination.getExamPrice());
        holder.inforText.setText(examination.getOtherInfor());
    }

    @Override
    public int getItemCount() {
        return examinationList.size();
    }
}
