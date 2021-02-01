package com.example.medicalhlepers.Forum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.medicalhelpers.R;

public class AskQuestion extends FragmentActivity {
    private View emptyView;
    private LinearLayout writeQuestion;
    private LinearLayout myCollect;
    private LinearLayout myQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        emptyView = findViewById(R.id.audio_empty_layout);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        writeQuestion = (LinearLayout) findViewById(R.id.writeQue);
        writeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestion.this, WriteQuestion.class);
                startActivity(intent);
            }
        });
        myCollect = (LinearLayout) findViewById(R.id.myCollect);
        myCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestion.this, MyCollector.class);
                startActivity(intent);
            }
        });
        myQuestion = (LinearLayout) findViewById(R.id.wodetiwen);
        myQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestion.this, MyQuestion.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        //关闭窗体动画显示
    }
}
