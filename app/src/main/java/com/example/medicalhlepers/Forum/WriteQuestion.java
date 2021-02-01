package com.example.medicalhlepers.Forum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WriteQuestion extends AppCompatActivity implements TextWatcher {
    private EditText questionText;
    private EditText buchongText;
    private TextView fabuText;
    private ImageView guanbiImage;
    private boolean flag = false;
    private List<PersonalMessageStore> personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
    private PersonalMessageStore personalMessageStore = personalMessageStoreList.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_question);
        initUi();
        fabuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag) {
                    sendQuestion();
                }
            }
        });
        guanbiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUi() {
        questionText = (EditText) findViewById(R.id.shuruwenti);
        questionText.addTextChangedListener(this);
        buchongText = (EditText) findViewById(R.id.wentineirong);
        buchongText.addTextChangedListener(this);
        fabuText = (TextView) findViewById(R.id.fabuwenti);
        guanbiImage = (ImageView) findViewById(R.id.guanbi);
    }

    private void sendQuestion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate);
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("announcer", personalMessageStore.getUserName()).
                            add("date", str).
                            add("title", questionText.getText().toString()).
                            add("category", "").
                            add("phone", personalMessageStore.getPhoneNumber()).
                            add("text", buchongText.getText().toString()).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/uploadQuestion").post(requestBody)
                            .build();
                    client.newCall(request).execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //监控edittext
    //一般我们都是在这个里面进行我们文本框的输入的判断，上面两个方法用到的很少
    @Override
    public void afterTextChanged(Editable s) {
        if(questionText.getText().toString().length() > 5) {    //问题是否超过了5个字符
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
}
