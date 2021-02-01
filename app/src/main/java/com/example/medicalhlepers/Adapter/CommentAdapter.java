package com.example.medicalhlepers.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Forum.CommentDao;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CommentAdapter extends ArrayAdapter<CommentDao> {
    private int resourceId;
    private PersonalMessageStore personalMessageStore;
    public CommentAdapter(Context context, int textViewResourceId,  //子控件的ID
                             List<CommentDao> objects, PersonalMessageStore personalMessageStore) {//数据
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.personalMessageStore = personalMessageStore;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CommentDao commentDao = getItem(position);
        View view;
        final CommentAdapter.ViewHolder viewHolder;
        boolean flag = false;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new CommentAdapter.ViewHolder();
            viewHolder.nameText = (TextView) view.findViewById(R.id.commentName);
            viewHolder.textText = (TextView) view.findViewById(R.id.commentText);
            viewHolder.likeNumText = (TextView) view.findViewById(R.id.zanNum);
            viewHolder.hasLikeImage = (ImageView) view.findViewById(R.id.zantong);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (CommentAdapter.ViewHolder) view.getTag();
        }
        viewHolder.nameText.setText(commentDao.getCommentName());
        viewHolder.textText.setText(commentDao.getText());
        viewHolder.likeNumText.setText(commentDao.getLikeNum()+"");
        if(commentDao.getHasLiked()) {  //如果点了赞
            viewHolder.hasLikeImage.setImageResource(R.drawable.zantong_selected);
        } else {
            viewHolder.hasLikeImage.setImageResource(R.drawable.zantong);
        }
        viewHolder.hasLikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentDao.getHasLiked()) {  //如果以前点了赞，取消赞
                    viewHolder.hasLikeImage.setImageResource(R.drawable.zantong);
                    viewHolder.likeNumText.setText(commentDao.getLikeNum()-1+"");
                    commentDao.setHasLiked(false);
                    commentDao.setLikeNum(commentDao.getLikeNum()-1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder().
                                        add("phone", personalMessageStore.getPhoneNumber()).
                                        add("sign", commentDao.getSign()+"").build();
                                Log.i("currentLocation", personalMessageStore.getPhoneNumber()+commentDao.getSign());
                                Request request = new Request.Builder().url("http://39.96.41.6:8080/reduceLikeNumForComment").post(requestBody)
                                        .build();
                                client.newCall(request).execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else {    //点赞
                    viewHolder.hasLikeImage.setImageResource(R.drawable.zantong_selected);
                    viewHolder.likeNumText.setText(commentDao.getLikeNum()+1+"");
                    commentDao.setHasLiked(true);
                    commentDao.setLikeNum(commentDao.getLikeNum()+1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder().
                                        add("phone", personalMessageStore.getPhoneNumber()).
                                        add("sign", commentDao.getSign()+"").build();
                                Log.i("currentLocation", personalMessageStore.getPhoneNumber()+commentDao.getSign());
                                Request request = new Request.Builder().url("http://39.96.41.6:8080/addLikeNumForComment").post(requestBody)
                                        .build();
                                client.newCall(request).execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        return view;
    }

    class ViewHolder {
        TextView nameText;
        TextView textText;
        TextView likeNumText;
        ImageView hasLikeImage;
    }


}
