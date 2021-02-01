package com.example.medicalhlepers.ChatRoom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalhelpers.R;
import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<Msg> mMsgList;

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView docImage;

        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout = itemView.findViewById(R.id.left_layout);
            rightLayout = itemView.findViewById(R.id.right_layout);
            leftMsg = itemView.findViewById(R.id.left_msg);
            rightMsg = itemView.findViewById(R.id.right_msg);
            docImage = itemView.findViewById(R.id.imageView8);
        }
    }

    /**
     * item内部的删除监听接口
     */

    public interface ItemInnerDeleteListener {
        void onItemInnerDeleteClick(int position);
    }

    private ItemInnerDeleteListener mItemInnerDeleteListener;

    public void setOnItemDeleteClickListener(ItemInnerDeleteListener mItemInnerDeleteListener) {
        this.mItemInnerDeleteListener = mItemInnerDeleteListener;
    }


    public MsgAdapter(List<Msg> mMsgList) {
        this.mMsgList = mMsgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.msg_item, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {
        Msg msg = mMsgList.get(i);

        if (msg.getType() == Msg.TYPE_RECEIVED) {
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
        } else {
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(msg.getContent());
        }

        viewHolder.docImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemInnerDeleteListener.onItemInnerDeleteClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

}