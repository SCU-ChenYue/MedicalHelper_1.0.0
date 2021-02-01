package com.example.medicalhlepers.ThreeMainFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.QuestionAdapter;
import com.example.medicalhlepers.Forum.AnswerDetail;
import com.example.medicalhlepers.Forum.AskQuestion;
import com.example.medicalhlepers.Forum.QuestionBeenAnswerItem;
import com.example.medicalhlepers.Forum.SearchTheAnswer;
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

public class FragmentLuntan extends Fragment {
    private SwipeRefreshLayout swipeRefresh;
    private ListView listView;
    private LinearLayout addLinear;
    private QuestionAdapter adapter;
    private List<QuestionBeenAnswerItem> list;
    private List<PersonalMessageStore> personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStoreList.get(0);
    private ImageView addQuestion;
    private Bundle bundle;
    private LinearLayout searchAnswer;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_luntan, container, false);
        list = new ArrayList<>();
        swipeRefresh = (SwipeRefreshLayout) contentView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        listView = (ListView) contentView.findViewById(R.id.questionList);
        addQuestion = (ImageView) contentView.findViewById(R.id.header_text_lift);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AskQuestion.class);
                startActivity(intent);
            }
        });
        addLinear = (LinearLayout) contentView.findViewById(R.id.header_layout_lift);
        addLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AskQuestion.class);
                startActivity(intent);
            }
        });

        initQuestionList();
        adapter = new QuestionAdapter(getActivity(), R.layout.question_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionBeenAnswerItem answerItem = list.get(position);
                Intent intent = new Intent(getActivity(), AnswerDetail.class);
                bundle = new Bundle();
                bundle.putSerializable("QuestionItem", answerItem);
                intent.putExtras(bundle);
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initQuestionList();
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });

        //搜索回答
        searchAnswer = (LinearLayout) contentView.findViewById(R.id.header_layout);
        searchAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchTheAnswer.class);
                startActivity(intent);
            }
        });

        return contentView;
    }

    private void initQuestionList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/getRecommendedQuestions").post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    list.clear();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        QuestionBeenAnswerItem answerItem = new QuestionBeenAnswerItem();
                        JSONObject jsonObject1 = jsonObject.getJSONObject("recommendAnswer");

                        answerItem.setQuestion(jsonObject.getString("title"));
                        answerItem.setDocName(jsonObject1.getString("responder"));
                        answerItem.setDocHospital(jsonObject1.getJSONObject("doctorInfo")
                                .getString("hosName"));
                        answerItem.setDocTitle(jsonObject1.getJSONObject("doctorInfo")
                                .getString("position"));
                        answerItem.setQuestionNum(jsonObject.getInt("answersNum"));
                        answerItem.setNumId(jsonObject.getInt("num"));  //问题的唯一标识
                        answerItem.setAnswer(jsonObject1.getString("text"));
                        answerItem.setLike(jsonObject1.getInt("likeNum"));
                        answerItem.setMark(jsonObject1.getInt("mark")); //回答的唯一标识
                        answerItem.setDescription(jsonObject.getString("text"));
                        answerItem.setImageUrl(jsonObject1.getJSONObject("doctorInfo")
                                .getString("imageUrl"));
                        answerItem.setDocLevel(jsonObject1.getJSONObject("doctorInfo")
                                .getString("level"));
                        answerItem.setDocGoodAt(jsonObject1.getJSONObject("doctorInfo")
                                .getString("goodAt"));
                        answerItem.setDepartment(jsonObject1.getJSONObject("doctorInfo")
                                .getString("departmentName"));
                        answerItem.setDocDescription(jsonObject1.getJSONObject("doctorInfo")
                                .getString("experience"));
                        answerItem.setCollectNum(jsonObject1.getInt("collectNum"));
                        answerItem.setIfHasliked(jsonObject1.getBoolean("ifHasLiked"));
                        answerItem.setIfHasCollected(jsonObject1.getBoolean("ifHasCollected"));
                        answerItem.setDate(jsonObject1.getString("date"));
                        answerItem.setCommentNum(jsonObject1.getInt("commentNum"));
                        Log.d("currentLocation", answerItem.getDocHospital()+answerItem.getDocName()+answerItem.getDocTitle());
                        list.add(answerItem);
                    }

                    Log.i("currentLocation", list.size() + "");
                    getActivity().runOnUiThread(new Runnable() {
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

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        initQuestionList();
    }
}
