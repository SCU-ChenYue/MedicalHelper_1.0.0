package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Forum.QuestionBeenAnswerItem;

import java.util.List;

public class DocAnswerAdapter extends ArrayAdapter<QuestionBeenAnswerItem> {
    private int resourceId;
    public DocAnswerAdapter(Context context, int textViewResourceId,  //子控件的ID
                             List<QuestionBeenAnswerItem> objects) {//数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuestionBeenAnswerItem questionBeenAnswerItem = getItem(position);
        View view;
        DocAnswerAdapter.ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new DocAnswerAdapter.ViewHolder();
            viewHolder.questionText = (TextView) view.findViewById(R.id.questionText);
            viewHolder.textText = (TextView) view.findViewById(R.id.tv_answer);
            viewHolder.likeNumText = (TextView) view.findViewById(R.id.tv_looks);
            viewHolder.commentNumText = (TextView) view.findViewById(R.id.tv_comments);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (DocAnswerAdapter.ViewHolder) view.getTag();
        }
        viewHolder.questionText.setText(questionBeenAnswerItem.getQuestion());
        viewHolder.textText.setText(questionBeenAnswerItem.getAnswer());
        viewHolder.likeNumText.setText(questionBeenAnswerItem.getLike()+" 人点赞");
        viewHolder.commentNumText.setText(questionBeenAnswerItem.getCommentNum()+" 人评论");
        return view;
    }

    class ViewHolder {
        TextView questionText;
        TextView textText;
        TextView likeNumText;
        TextView commentNumText;
    }
}
