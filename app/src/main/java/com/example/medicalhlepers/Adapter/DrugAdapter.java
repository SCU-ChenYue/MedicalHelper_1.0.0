package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.ChatRoom.MsgAdapter;
import com.example.medicalhlepers.drugQuery.DrugDescriptionActivity;
import com.example.medicalhlepers.drugQuery.Drug_item;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.ViewHolder> {
    private Context mContext;
    private List<Drug_item> drug_itemList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View drugView;
        CardView cardView;
        SimpleDraweeView simpleDraweeView;
        TextView drugName;

        public ViewHolder(View view) {
            super(view);
            drugView = view;
            cardView = (CardView) view;
            simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.drug_image);
            drugName = (TextView) view.findViewById(R.id.drug_name);
        }
    }

    public DrugAdapter(List<Drug_item> list) {
        drug_itemList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.drug_item2,
                parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Drug_item drug_item = drug_itemList.get(position);
                Intent intent=new Intent(mContext, DrugDescriptionActivity.class);
                intent.putExtra("name", drug_item.getName());
                intent.putExtra("description", drug_item.getDes());
                intent.putExtra("imageUrl", drug_item.getImageUrl());
                intent.putExtra("price", drug_item.getPrice());
                intent.putExtra("type", drug_item.getType());
                intent.putExtra("specs", drug_item.getSpecs());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Drug_item drug_item = drug_itemList.get(position);
        holder.drugName.setText(drug_item.getName());
        holder.simpleDraweeView.setImageURI(Uri.parse(drug_item.getImageUrl()));
    }

    @Override
    public int getItemCount() {
        return drug_itemList.size();
    }
}
