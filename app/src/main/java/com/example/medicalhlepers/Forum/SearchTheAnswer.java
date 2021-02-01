package com.example.medicalhlepers.Forum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class SearchTheAnswer extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private ImageView back;
    private EditText editText;
    private ImageView deleteInput;
    private TextView searchText;
    private QuestionAdapter adapter;
    private List<QuestionBeenAnswerItem> list = new ArrayList<>();
    private ListView listView;
    private List<PersonalMessageStore> personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStoreList.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_the_answer);
        back = (ImageView) findViewById(R.id.header_text);
        editText = (EditText) findViewById(R.id.auto_edit);
        deleteInput = (ImageView) findViewById(R.id.ivDeleteText);
        searchText = (TextView) findViewById(R.id.searchbutton);

        back.setOnClickListener(this);
        deleteInput.setOnClickListener(this);
        searchText.setOnClickListener(this);
        editText.addTextChangedListener(this);

        adapter = new QuestionAdapter(SearchTheAnswer.this, R.layout.question_item, list);
        listView = (ListView) findViewById(R.id.historyRecord);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionBeenAnswerItem answerItem = list.get(position);
                Bundle bundle = new Bundle();
                Intent intent = new Intent(SearchTheAnswer.this, AnswerDetail.class);
                bundle = new Bundle();
                bundle.putSerializable("QuestionItem", answerItem);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void sendInput(final String input) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("kd", input).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/queryQuestionsBeenAnswered").
                            post(requestBody).build();
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
                        Log.d("currentLocation", answerItem.getDocHospital() + answerItem.getDocName() + answerItem.getDocTitle());
                        list.add(answerItem);
                    }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_text:
                finish();
                break;
            case R.id.ivDeleteText:
                editText.setText("");
                break;
            case R.id.searchbutton:
                sendInput(editText.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(editText.getText().toString().equals("")) {  //如果是空的
            list.clear();
            adapter.notifyDataSetChanged();
        } else {
            sendInput(editText.getText().toString());
        }
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }


}
