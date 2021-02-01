package com.example.medicalhlepers.Forum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicalhelpers.R;
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

/**
 * 查看被我收藏了的回答
 */
public class MyCollector extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefresh;
    private ListView listView;
    private TextView shoucangText;
    private ImageView backImage;
    private QuestionAdapter adapter;
    private List<QuestionBeenAnswerItem> list = new ArrayList<>();
    private List<PersonalMessageStore> personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStoreList.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collector);
        Intent intent = getIntent();
        initUi();
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        initCollectList();
        adapter = new QuestionAdapter(this, R.layout.shoucang_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionBeenAnswerItem answerItem = list.get(position);
                Intent intent = new Intent(MyCollector.this, AnswerDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("QuestionItem", answerItem);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {  //刷新
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                       runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initCollectList();
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
    }

    private void initUi() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        listView = (ListView) findViewById(R.id.shoucangList);
        shoucangText = (TextView) findViewById(R.id.shoucangNum);
        backImage = (ImageView) findViewById(R.id.img_back);
    }

    private void initCollectList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/queryCollectionsForUser").post(requestBody)
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
                        answerItem.setIfHasCollected(jsonObject.getBoolean("ifHasCollected"));
                        answerItem.setIfHasliked(jsonObject.getBoolean("ifHasLiked"));
                        Log.d("currentLocation", answerItem.getDocHospital()+answerItem.getDocName()+answerItem.getDocTitle());
                        list.add(answerItem);
                    }
                    Log.i("currentLocation", list.size() + "");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shoucangText.setText("共"+list.size()+"个内容");
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        initCollectList();
    }
}
