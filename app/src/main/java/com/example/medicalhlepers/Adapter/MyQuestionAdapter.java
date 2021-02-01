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

public class MyQuestionAdapter extends ArrayAdapter<QuestionBeenAnswerItem> {
    private int resourceId;
    public MyQuestionAdapter(Context context, int textViewResourceId,  //子控件的ID
                             List<QuestionBeenAnswerItem> objects) {//数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuestionBeenAnswerItem questionBeenAnswerItem = getItem(position);
        View view;
        MyQuestionAdapter.ViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new MyQuestionAdapter.ViewHolder();
            viewHolder.questionText = (TextView) view.findViewById(R.id.questionText);
            viewHolder.answerNumText = (TextView) view.findViewById(R.id.answerNum);
            viewHolder.dateText = (TextView) view.findViewById(R.id.answerDate);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (MyQuestionAdapter.ViewHolder) view.getTag();
        }
        viewHolder.questionText.setText(questionBeenAnswerItem.getQuestion());
        viewHolder.answerNumText.setText(questionBeenAnswerItem.getQuestionNum()+" 回答");
        viewHolder.dateText.setText(questionBeenAnswerItem.getDate());
        return view;
    }

    class ViewHolder {
        TextView questionText;
        TextView answerNumText;
        TextView dateText;
    }
}
