package com.example.medicalhlepers.Test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.VoiceRecognitionService;
import com.baidu.speech.asr.SpeechConstant;
import com.example.medicalhelpers.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Voice_index extends AppCompatActivity implements EventListener {
    private Button voice_button;
    private EventManager asr;
    private boolean logTime = true;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_index);
        initPermission();
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener中onEvent方法，注册自己的事件输出类
        voice_button=findViewById(R.id.voice_button);
        voice_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:   //按住的时候执行start
                        start();
                        /*mStartSpeechButton.setBackgroundResource(
                                R.drawable.bdspeech_btn_orangelight_pressed);*/
                        break;
                    case MotionEvent.ACTION_UP:
                        stop();
                        /*mStartSpeechButton.setBackgroundResource(
                                R.drawable.bdspeech_btn_orangelight_normal);*/
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        textView=findViewById(R.id.voice_test_text);
    }

    /**
     * 测试参数填在这里
     */
    private void start() {
        Toast.makeText(Voice_index.this, "请开始说话", Toast.LENGTH_SHORT).show();
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event
//        if (enableOffline){
//            params.put(SpeechConstant.DECODER, 2);
//        }
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 2000);
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        //  params.put(SpeechConstant.OUT_FILE, "/storage/emulated/0/baiduASR/outfile.pcm");
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);
        params.put(SpeechConstant.DISABLE_PUNCTUATION, false);
        //  params.put(SpeechConstant.NLU, "enable");
        //  params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 800);
        //  params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        //  params.put(SpeechConstant.PROP ,20000);
        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        printLog("输入参数：" + json);
        asr.send(event, json, null, 0, 0);  //发送start开始事件
    }

    /*
     * EventListener  回调方法
     * name:回调事件（事件名）
     * params: JSON数据（事件参数）
     */
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String resultTxt = null;
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)){    //识别结果参数
            if (params.contains("\"final_result\"")){   //语义结果值
                try {
                    JSONObject json = new JSONObject(params);
                    String result = json.getString("best_result");  //取得key的识别结果
                    resultTxt = result;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (resultTxt != null){
            resultTxt += "\n";
            textView.append(resultTxt);
        }
        String result = "name" + name;

        if (length > 0 && data.length > 0) {
            result += ", 语义解析结果：" + new String(data, offset, length);
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            result += "引擎准备就绪，可以开始说话";

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            result += "检测到用户的已经开始说话";

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            result += "检测到用户的已经停止说话";
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            // 临时识别结果, 长语音模式需要从此消息中取出结果
            result += "识别临时识别结果";
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
//            Log.d(TAG, "Temp Params:"+result);
            System.out.println("Temp Params:"+result);

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束， 最终识别结果或可能的错误
            result += "识别结束";
            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
            //Log.d(TAG, "Result Params:"+result);
            System.out.println("Result Params:"+result);

        }
    }

    private void printLog(String text) {
        if (logTime) {
            text += "  ;time=" + System.currentTimeMillis();
        }
        text += "\n";
        Log.i(getClass().getName(), text);
//        txtLog.append(text + "\n");
    }


    private void stop() {
        textView.append("录音结果:");
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //发送停止录音事件
    }
    /**
     *  * android 6.0 以上需要动态申请权限
     *  
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        asr.unregisterListener(this);//退出事件管理器
    }
}
