package com.example.medicalhlepers.Forum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.CommentAdapter;
import com.example.medicalhlepers.BasicActivity.MainActivity;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WriteComment extends AppCompatActivity implements TextWatcher {
    private QuestionBeenAnswerItem questionItem;
    private CommentAdapter1 adapter;
    private EditText editText;
    private TextView fabuText;
    private TextView commentText;
    private ImageView guanImage;
    private ListView listView;
    private boolean flag;
    private List<PersonalMessageStore> personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStoreList.get(0);
    private List<CommentDao> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);
        Intent intent = getIntent();
        questionItem = (QuestionBeenAnswerItem) intent.getSerializableExtra("QuestionItem");
        initUi();
        initCommentList();
        adapter = new CommentAdapter1(this, R.layout.comment_item, list);
        listView.setAdapter(adapter);
        fabuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
                flag = false;
                fabuText.setTextColor(0xFF888888);
                editText.setText("");
            }
        });
        guanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
    }

    private void initUi() {
        editText = (EditText) findViewById(R.id.writeComment);
        editText.addTextChangedListener(this);
        fabuText = (TextView) findViewById(R.id.fabupinglun);
        listView = (ListView) findViewById(R.id.commentList);
        guanImage = (ImageView) findViewById(R.id.guanbi);
        commentText = (TextView) findViewById(R.id.commentNum);
    }

    @Override
    public void finish() {
        super.finish();
        //关闭窗体动画显示
    }

    private void initCommentList() {    //查看该回答下的所有评论
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("mark", questionItem.getMark()+"").build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/queryCommentsForAnswer").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    list.clear();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CommentDao commentDao = new CommentDao();
                        commentDao.setCommentName(jsonObject.getString("commenter"));
                        commentDao.setPhone(jsonObject.getString("phone"));
                        commentDao.setMark(jsonObject.getInt("mark"));
                        commentDao.setText(jsonObject.getString("text"));
                        commentDao.setLikeNum(jsonObject.getInt("likeNum"));
                        commentDao.setSign(jsonObject.getInt("sign"));
                        commentDao.setHasLiked(jsonObject.getBoolean("ifHasLiked"));
                        list.add(commentDao);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            commentText.setText("全部 "+list.size()+" 条评论");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendComment() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().
                            add("commenter", personalMessageStore.getUserName()).
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("mark", questionItem.getMark()+"").
                            add("text", editText.getText().toString()).
                            add("likeNum", 0+"").build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/uploadComment").post(requestBody)
                            .build();
                    client.newCall(request).execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initCommentList();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCommentList();
    }

    @Override   //常用
    public void afterTextChanged(Editable s) {
        if(editText.getText().toString().length() > 0) {    //问题是否超过了5个字符
            flag = true;
            fabuText.setTextColor(0xFF1296DB);
        } else {
            flag = false;
            fabuText.setTextColor(0xFF888888);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    public class CommentAdapter1 extends ArrayAdapter<CommentDao> {
        private int resourceId;
        public CommentAdapter1(Context context, int textViewResourceId,  //子控件的ID
                              List<CommentDao> objects) {//数据
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final CommentDao commentDao = getItem(position);
            View view;
            if(convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            } else {
                view = convertView;
            }
            TextView nameText = (TextView) view.findViewById(R.id.commentName);
            TextView textText = (TextView) view.findViewById(R.id.commentText);
            final TextView likeNumText = (TextView) view.findViewById(R.id.zanNum);
            final ImageView hasLikeImage = (ImageView) view.findViewById(R.id.zantong);
            nameText.setText(commentDao.getCommentName());
            textText.setText(commentDao.getText());
            likeNumText.setText(commentDao.getLikeNum()+"");
            if(commentDao.getHasLiked()) {  //如果点了赞
                hasLikeImage.setImageResource(R.drawable.zantong_selected);
            } else {
                hasLikeImage.setImageResource(R.drawable.zantong);
            }

            hasLikeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(commentDao.getHasLiked()) {  //如果赞过，则取消赞
                        hasLikeImage.setImageResource(R.drawable.zantong);
                        likeNumText.setText(commentDao.getLikeNum()-1+"");
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            initCommentList();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else { //如果没赞过，就是点赞
                        hasLikeImage.setImageResource(R.drawable.zantong_selected);
                        likeNumText.setText(commentDao.getLikeNum()+1+"");
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            initCommentList();
                                        }
                                    });
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
    }
}


