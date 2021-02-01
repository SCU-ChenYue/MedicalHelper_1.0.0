package com.example.medicalhlepers.Forum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.AnswerListAdapter;
import com.example.medicalhlepers.Adapter.QuestionAdapter;
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

public class AllAnswersOfOneQuestion extends AppCompatActivity {
    private TextView questionText;
    private TextView answerNumText;
    private TextView descriptionText;
    private ListView listView;
    private QuestionBeenAnswerItem questionItem;
    private ImageView backImage;
    private String question;
    private int num, answerNum;
    private List<QuestionBeenAnswerItem> list = new ArrayList<>();
    private AnswerListAdapter adapter;
    private List<PersonalMessageStore> personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStoreList.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_answers_of_one_question);
        Intent intent = getIntent();
        questionItem = (QuestionBeenAnswerItem) intent.getSerializableExtra("QuestionItem");
        question = questionItem.getQuestion();
        num = questionItem.getNumId();
        answerNum = questionItem.getQuestionNum();

        questionText = (TextView) findViewById(R.id.tv_question);
        questionText.setText(question);
        listView = (ListView) findViewById(R.id.answerList);
        answerNumText = (TextView) findViewById(R.id.answerNum1);
        answerNumText.setText(answerNum+"个回答");
        descriptionText = (TextView) findViewById(R.id.questionDescribe);
        descriptionText.setText(questionItem.getDescription());
        backImage = (ImageView) findViewById(R.id.img_back);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        answerList();
    }

    private void swipeList() {
        adapter = new AnswerListAdapter(this, R.layout.answer_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionBeenAnswerItem answerItem = list.get(position);
                answerItem.setQuestion(question);
                Intent intent = new Intent(AllAnswersOfOneQuestion.this, AnswerDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("QuestionItem", answerItem);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void answerList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list.clear();
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("num", num+"").
                            add("phone", personalMessageStore.getPhoneNumber()).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/queryAnswers").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        QuestionBeenAnswerItem answerItem = new QuestionBeenAnswerItem();
                        answerItem.setDocName(jsonObject.getString("responder"));
                        answerItem.setDocHospital(jsonObject.getJSONObject("doctorInfo")
                                .getString("hosName"));
                        answerItem.setDocTitle(jsonObject.getJSONObject("doctorInfo")
                                .getString("position"));
                        answerItem.setAnswer(jsonObject.getString("text"));
                        answerItem.setLike(jsonObject.getInt("likeNum"));
                        answerItem.setImageUrl(jsonObject.getJSONObject("doctorInfo")
                                .getString("imageUrl"));
                        answerItem.setDocLevel(jsonObject.getJSONObject("doctorInfo")
                                .getString("level"));
                        answerItem.setDocGoodAt(jsonObject.getJSONObject("doctorInfo")
                                .getString("goodAt"));
                        answerItem.setDepartment(jsonObject.getJSONObject("doctorInfo")
                                .getString("departmentName"));
                        answerItem.setDocDescription(jsonObject.getJSONObject("doctorInfo")
                                .getString("experience"));

                        answerItem.setMark(jsonObject.getInt("mark"));
                        answerItem.setCollectNum(jsonObject.getInt("collectNum"));
                        answerItem.setIfHasliked(jsonObject.getBoolean("ifHasLiked"));
                        answerItem.setIfHasCollected(jsonObject.getBoolean("ifHasCollected"));
                        answerItem.setDate(jsonObject.getString("date"));
                        answerItem.setCommentNum(jsonObject.getInt("commentNum"));
                        Log.d("currentLocation", answerItem.getDocHospital()+answerItem.getDocName()+answerItem.getDocTitle());
                        list.add(answerItem);
                    }
                    Log.i("currentLocation", list.size()+"");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeList();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
