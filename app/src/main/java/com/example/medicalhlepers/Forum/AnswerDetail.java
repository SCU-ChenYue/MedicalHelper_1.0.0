package com.example.medicalhlepers.Forum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.DiagnoseOnline.OnlineDoc;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;
import com.facebook.drawee.view.SimpleDraweeView;

import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AnswerDetail extends AppCompatActivity implements View.OnClickListener{
    private TextView questionText;
    private TextView docNameText;
    private TextView commentText;
    private LinearLayout zantongClick;
    private LinearLayout shoucangClick;
    private ImageView zantongImage;
    private ImageView shoucangImage;
    private TextView hospitalText;
    private TextView titleText;
    private TextView answerText;
    private TextView markText;
    private TextView likeText;
    private TextView dateText;
    private SimpleDraweeView docImage;
    private LinearLayout allAnswer;
    private LinearLayout docMessage;
    private LinearLayout comment;
    private ImageView backImage;
    private QuestionBeenAnswerItem questionItem;
    private String question, docName, hospital, title, answer;
    private int num, answerNum, likeNum, markNum, collectNum;
    boolean isChanged = false;
    boolean isChanged2 = false;
    private List<PersonalMessageStore> personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStoreList.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail);
        Intent intent = getIntent();
        initUi();
        questionItem = (QuestionBeenAnswerItem) intent.getSerializableExtra("QuestionItem");
        question = questionItem.getQuestion();
        docName = questionItem.getDocName();
        hospital = questionItem.getDocHospital();
        title = questionItem.getDocTitle();
        answer = questionItem.getAnswer();
        num = questionItem.getNumId();
        answerNum = questionItem.getQuestionNum();
        likeNum = questionItem.getLike();
        markNum = questionItem.getMark();
        collectNum = questionItem.getCollectNum();
        isChanged = questionItem.isIfHasliked();
        isChanged2 = questionItem.isIfHasCollected();

        allAnswer.setOnClickListener(this);
        zantongClick.setOnClickListener(this);
        shoucangClick.setOnClickListener(this);
        docMessage.setOnClickListener(this);
        comment.setOnClickListener(this);
        backImage.setOnClickListener(this);

        questionText.setText(question);
        docNameText.setText(docName);
        hospitalText.setText(hospital);
        titleText.setText(title);
        answerText.setText(answer);
        dateText.setText("编辑于 "+ questionItem.getDate());
        likeText.setText("赞同"+likeNum);
        markText.setText("收藏"+collectNum);  //收藏数
        commentText.setText("评论"+questionItem.getCommentNum());
        docImage.setImageURI(questionItem.getImageUrl());
        if(isChanged) {
            zantongImage.setImageResource(R.drawable.zantong_selected);
        }else {
            zantongImage.setImageResource(R.drawable.zantong);
        }

        if(isChanged2) {
            shoucangImage.setImageResource(R.drawable.shoucang_selected);
        } else {
            shoucangImage.setImageResource(R.drawable.shoucang);
        }
    }

    private void initUi() {
        questionText = (TextView) findViewById(R.id.tv_question);
        docNameText = (TextView) findViewById(R.id.docName);
        hospitalText = (TextView) findViewById(R.id.docAnswerHospital);
        titleText = (TextView) findViewById(R.id.docAnswerTitle);
        dateText = (TextView) findViewById(R.id.writeDate);
        answerText = (TextView) findViewById(R.id.tv_answer);
        zantongClick = (LinearLayout) findViewById(R.id.ll_recommend);
        allAnswer = (LinearLayout) findViewById(R.id.allAnswer);
        zantongImage = (ImageView) findViewById(R.id.agree);
        likeText = (TextView) findViewById(R.id.tv_recommend);
        markText = (TextView) findViewById(R.id.markText);
        shoucangClick = (LinearLayout) findViewById(R.id.ll_follow);
        shoucangImage = (ImageView) findViewById(R.id.img_follow);
        docImage = (SimpleDraweeView) findViewById(R.id.imageView10);
        docMessage = (LinearLayout) findViewById(R.id.docMessage);
        comment = (LinearLayout) findViewById(R.id.ll_comment);
        backImage = (ImageView) findViewById(R.id.img_back);
        commentText = (TextView) findViewById(R.id.commentNum);
    }

    private void refreshUi() {
        likeNum = questionItem.getLike();
        collectNum = questionItem.getCollectNum();
        likeText.setText("赞同"+likeNum);
        markText.setText("收藏"+collectNum);  //收藏数
        commentText.setText("评论"+questionItem.getCommentNum());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allAnswer:    //查看该问题的所有回答
                Intent intent = new Intent(AnswerDetail.this, AllAnswersOfOneQuestion.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("QuestionItem", questionItem);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.ll_recommend: //赞同
                if(isChanged){  //取消赞
                    zantongImage.setImageResource(R.drawable.zantong);
                    questionItem.setIfHasliked(false);
                    subLikeNum();
                }else {         //点赞
                    zantongImage.setImageResource(R.drawable.zantong_selected);
                    questionItem.setIfHasliked(true);
                    addLikeNum();
                }
                isChanged = !isChanged;
                break;
            case R.id.ll_follow:    //收藏
                if(isChanged2){     //取消收藏
                    shoucangImage.setImageResource(R.drawable.shoucang);
                    questionItem.setIfHasCollected(false);
                    subCollecteNum();
                }else {             //收藏
                    shoucangImage.setImageResource(R.drawable.shoucang_selected);
                    questionItem.setIfHasCollected(true);
                    addCollectNum();
                }
                isChanged2 = !isChanged2;
                break;
            case R.id.docMessage:
                Intent intent2 = new Intent(AnswerDetail.this, OnlineDoc.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("QuestionItem", questionItem);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;

            case R.id.ll_comment:
                Intent intent3 = new Intent(AnswerDetail.this, WriteComment.class);
                Bundle bundle3 = new Bundle();
                bundle3.putSerializable("QuestionItem", questionItem);
                intent3.putExtras(bundle3);
                startActivity(intent3);
                break;

            case R.id.img_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void addLikeNum() { //点赞数++
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("mark", questionItem.getMark()+"").build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/addLikeNum").post(requestBody)
                            .build();
                    client.newCall(request).execute();
                    questionItem.setLike(questionItem.getLike()+1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUi();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void subLikeNum() { //点赞数--
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("mark", questionItem.getMark()+"").build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/decreaseLikeNum").post(requestBody)
                            .build();
                    client.newCall(request).execute();
                    questionItem.setLike(questionItem.getLike()-1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUi();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addCollectNum() { //收藏数++
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("mark", questionItem.getMark()+"").build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/addCollection").post(requestBody)
                            .build();
                    client.newCall(request).execute();
                    questionItem.setCollectNum(questionItem.getCollectNum()+1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUi();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void subCollecteNum() { //收藏数--
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("mark", questionItem.getMark()+"").build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/decreaseCollection").post(requestBody)
                            .build();
                    client.newCall(request).execute();
                    questionItem.setCollectNum(questionItem.getCollectNum()-1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUi();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
