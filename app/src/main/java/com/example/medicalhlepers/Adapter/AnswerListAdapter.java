package com.example.medicalhlepers.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Forum.QuestionBeenAnswerItem;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class AnswerListAdapter extends ArrayAdapter<QuestionBeenAnswerItem> {
    private int resourceId;
    public AnswerListAdapter(Context context, int textViewResourceId,  //子控件的ID
                           List<QuestionBeenAnswerItem> objects) {//数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuestionBeenAnswerItem questionBeenAnswerItem = getItem(position);
        View view;
        AnswerListAdapter.ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new AnswerListAdapter.ViewHolder();
            viewHolder.doctorText = (TextView) view.findViewById(R.id.docAnswer);
            viewHolder.answerText = (TextView) view.findViewById(R.id.tv_answer);
            viewHolder.titleText = (TextView) view.findViewById(R.id.docAnswerTitle);
            viewHolder.hospitalText = (TextView) view.findViewById(R.id.docAnswerHospital);
            viewHolder.likeNumText = (TextView) view.findViewById(R.id.tv_looks);
            viewHolder.docImage = (SimpleDraweeView) view.findViewById(R.id.imageView9);
            viewHolder.commentNumText = (TextView) view.findViewById(R.id.tv_comments);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (AnswerListAdapter.ViewHolder) view.getTag();
        }
        viewHolder.doctorText.setText(questionBeenAnswerItem.getDocName());
        viewHolder.answerText.setText(questionBeenAnswerItem.getAnswer());
        viewHolder.titleText.setText(questionBeenAnswerItem.getDocTitle());
        viewHolder.hospitalText.setText(questionBeenAnswerItem.getDocHospital());
        viewHolder.likeNumText.setText(questionBeenAnswerItem.getLike()+"人点赞");
        viewHolder.commentNumText.setText(questionBeenAnswerItem.getCommentNum()+" 人评论");
        viewHolder.docImage.setImageURI(questionBeenAnswerItem.getImageUrl());
        return view;
    }

    class ViewHolder {
        TextView doctorText;
        TextView answerText;
        TextView titleText;
        TextView hospitalText;
        TextView likeNumText;
        TextView commentNumText;
        SimpleDraweeView docImage;
    }
}
