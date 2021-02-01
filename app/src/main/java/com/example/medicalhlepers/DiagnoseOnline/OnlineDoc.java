package com.example.medicalhlepers.DiagnoseOnline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.DocAnswerAdapter;
import com.example.medicalhlepers.ChatRoom.ChatRoom;
import com.example.medicalhlepers.Forum.AnswerDetail;
import com.example.medicalhlepers.Forum.QuestionBeenAnswerItem;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;
import com.facebook.drawee.view.SimpleDraweeView;

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

public class OnlineDoc extends AppCompatActivity {
    private String docName;
    private String status;  //在线状态
    private String userName;
    private ImageView shoucangImage;
    private TextView docNameText;
    private TextView docTitleText;
    private TextView hospitalText;
    private TextView departmentText;
    private TextView jianjieText;
    private TextView guanzhuText;
    private SimpleDraweeView image;
    private ImageView shenqingBut;
    private QuestionBeenAnswerItem questionItem;
    boolean isChanged2 = false;
    private ListView listView;
    private List<QuestionBeenAnswerItem> list = new ArrayList<>();
    private List<PersonalMessageStore> personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStoreList.get(0);
    private DocAnswerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_doc);
        Intent intent = getIntent();
        questionItem = (QuestionBeenAnswerItem) intent.getSerializableExtra("QuestionItem");
        //初始化控件
        docNameText = (TextView) findViewById(R.id.textView21);
        docTitleText = (TextView) findViewById(R.id.textView22);
        hospitalText = (TextView) findViewById(R.id.textView23);
        departmentText = (TextView) findViewById(R.id.textView24);
        jianjieText = (TextView) findViewById(R.id.jianjie);
        guanzhuText = (TextView) findViewById(R.id.guanzhu);
        shenqingBut = (ImageView) findViewById(R.id.img_zixun);
        shoucangImage = (ImageView) findViewById(R.id.img_follow);
        image = (SimpleDraweeView) findViewById(R.id.imageView8);
        showUI();
        isChanged2 = questionItem.isIfHasCollected();
        if(isChanged2) {    //收藏了
            shoucangImage.setImageResource(R.drawable.shoucang_selected);
            guanzhuText.setText("已关注");
        } else {
            shoucangImage.setImageResource(R.drawable.shoucang);
            guanzhuText.setText("未关注");
        }
        listView = (ListView) findViewById(R.id.answerList);
        initDocAnswerList();
        adapter = new DocAnswerAdapter(this, R.layout.answer2_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionBeenAnswerItem answerItem = list.get(position);
                Intent intent = new Intent(OnlineDoc.this, AnswerDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("QuestionItem", answerItem);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initDocAnswerList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("name", questionItem.getDocName()).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/queryAnswersForDoctor").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    list.clear();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        QuestionBeenAnswerItem answerItem = new QuestionBeenAnswerItem();
                        JSONObject docObject = jsonObject.getJSONObject("doctorInfo");
                        JSONObject queObject = jsonObject.getJSONObject("question");
                        answerItem.setDocName(jsonObject.getString("responder"));
                        answerItem.setDate(jsonObject.getString("date"));
                        answerItem.setAnswer(jsonObject.getString("text"));
                        answerItem.setDocHospital(docObject.getString("hosName"));
                        answerItem.setDocTitle(docObject.getString("position"));
                        answerItem.setDocLevel(docObject.getString("level"));
                        answerItem.setImageUrl(docObject.getString("imageUrl"));
                        answerItem.setDocGoodAt(docObject.getString("goodAt"));
                        answerItem.setDocDescription(docObject.getString("experience"));
                        answerItem.setCollectNum(jsonObject.getInt("collectNum"));
                        answerItem.setLike(jsonObject.getInt("likeNum"));
                        answerItem.setCommentNum(jsonObject.getInt("commentNum"));
                        answerItem.setMark(jsonObject.getInt("mark"));
                        answerItem.setQuestionNum(queObject.getInt("answersNum"));
                        answerItem.setAskName(queObject.getString("announcer"));
                        answerItem.setAskDate(queObject.getString("date"));
                        answerItem.setQuestion(queObject.getString("title"));
                        answerItem.setDescription(queObject.getString("text"));
                        answerItem.setNumId(queObject.getInt("num"));
                        answerItem.setIfHasliked(jsonObject.getBoolean("ifHasLiked"));
                        answerItem.setIfHasCollected(jsonObject.getBoolean("ifHasCollected"));
                        list.add(answerItem);
                    }

                    Log.i("currentLocation", list.size() + "?");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showUI() {
        docTitleText.setText(questionItem.getDocTitle());
        hospitalText.setText(questionItem.getDocHospital());
        departmentText.setText(questionItem.getDepartment());
        jianjieText.setText(questionItem.getDocDescription());
        docNameText.setText(questionItem.getDocName());
        image.setImageURI(questionItem.getImageUrl());
        shenqingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnlineDoc.this, ChatRoom.class);
                intent.putExtra("userName", personalMessageStore.getUserName());
                intent.putExtra("duifang", questionItem.getDocName());
                intent.putExtra("status", "");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initDocAnswerList();
    }
}
