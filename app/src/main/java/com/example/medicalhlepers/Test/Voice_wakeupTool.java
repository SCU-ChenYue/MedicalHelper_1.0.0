package com.example.medicalhlepers.Test;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.medicalhlepers.drugQuery.Drug_main_activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Voice_wakeupTool {
    public static final String TAG = Voice_wakeup.class.getSimpleName();
    private Context context;
    private EventManager mWpEventManager;

    public Voice_wakeupTool(Context context) {
        this.context = context;
        mWpEventManager = EventManagerFactory.create(context, "wp");
        //注册监听事件
        mWpEventManager.registerListener(new Voice_wakeupTool.MyEventListener());
    }

    /**
     * 开启唤醒功能
     */
    public void start() {
        HashMap<String, String> params = new HashMap<String, String>();
        // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        mWpEventManager.send(SpeechConstant.WAKEUP_START, new JSONObject(params).toString(), null, 0, 0);
        Log.d(TAG, "----->唤醒已经开始工作了");
    }
    /**
     * 关闭唤醒功能
     */
    public void stop() {
        // 具体参数的百度没有具体说明，大体需要以下参数
        // send(String arg1, byte[] arg2, int arg3, int arg4)
        mWpEventManager.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
        Log.d(TAG, "----->唤醒已经停止");
    }
    private class MyEventListener implements EventListener
    {
        @Override
        public void onEvent(String name, String params, byte [] data, int
                offset, int length) {
            String result = null;
            Log.d(TAG, String.format("event: name=%s, params=%s", name, params));
            //唤醒事件
            if(name.equals("wp.data")){
                try {
                    JSONObject json = new JSONObject(params);
                    String word = json.getString("word");
                    result = word;
                    int errorCode = json.getInt("errorCode");
                    if(errorCode == 0){
                        //唤醒成功
                        Log.d(TAG, "唤醒成功");
                        if(word.equals("帮我买药")) {
                            Intent intent = new Intent(context, Drug_main_activity.class);
                            context.startActivity(intent);
                        }
                    } else {
                        //唤醒失败
                        Log.d(TAG, "唤醒失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if("wp.exit".equals(name)){
                //唤醒已停止
            }
            if (result != null){
                result += "\n";
                //textView.append(result);
            }
        }
    }
}
