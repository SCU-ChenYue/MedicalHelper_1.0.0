package com.example.medicalhlepers.RecordHelper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.AIdiagnose.ChangeSetting;
import com.example.medicalhlepers.AIdiagnose.DiagonseResultMain;
import com.example.medicalhlepers.AIdiagnose.Hospital_2;
import com.example.medicalhlepers.AIdiagnose.OtherSymptoms;
import com.example.medicalhlepers.AIdiagnose.Symptom;
import com.example.medicalhlepers.Dialog.MyProgressDialog;
import com.baidu.speech.EventListener;
import com.example.medicalhlepers.MapUse.AMapUtils;
import com.example.medicalhlepers.MapUse.LngLat;
import com.example.medicalhlepers.MapUse.MapUse;
import com.example.medicalhlepers.Tools.AnalyzeActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AudioRecordActivity extends FragmentActivity implements View.OnClickListener ,EventListener {
    private MyProgressDialog dialog;
    private ImageView recordAudioView;
    private ImageView ivSet;
    private View emptyView;
    private LineWaveVoiceView mHorVoiceView;
    private TextView tvRecordTips;
    private LinearLayout layoutCancelView;
    private String[] recordStatusDescription;
    public static final String KEY_ENTER_RECORD_AUDIO_ENTITY = "enter_record_audio";
    public static final String KEY_AUDIO_BUNDLE = "audio_bundle";
    private Timer timer;
    private TimerTask timerTask;
    public static final long DEFAULT_MAX_RECORD_TIME = 600000;
    public static final long DEFAULT_MIN_RECORD_TIME = 2000;
    protected static final int DEFAULT_MIN_TIME_UPDATE_TIME = 1000;
    private long maxRecordTime = DEFAULT_MAX_RECORD_TIME;
    private long minRecordTime = DEFAULT_MIN_RECORD_TIME;
    private long recordTotalTime;
    private Handler mainHandler;
    private EventManager asr;
    private boolean logTime = true;
    private TextView textView;
    private String input;
    private static final String alihttp="http://101.201.151.125:8000/";
    private double lat; //用户的
    private double lon;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    private List<Symptom> list;
    private int distance = 50;
    private int distancePercent = 50;
    private int levelPercent = 50;
    private String hospitalchooseType;
    private String hospitalchooseLevel;
    private String docchooseLevel;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_feed);
        list = new ArrayList<>();
        //editor.apply();
        startLocaion();
        initPermission();
        SharedPreferences pref = getSharedPreferences("setting1", MODE_PRIVATE);
        hospitalchooseType = pref.getString("hospitalchooseType", "");
        hospitalchooseLevel = pref.getString("hospitalchooseLevel", "");
        docchooseLevel = pref.getString("docchooseLevel", "");
        distance = pref.getInt("distance", 100);
        distancePercent = pref.getInt("distancePercent", 50);
        levelPercent = pref.getInt("levelPercent", 50);
        Log.i("currentLocation", "读出来的"+lat+" "+distance+" "+levelPercent+
                " "+distancePercent+" "+hospitalchooseType+" "+hospitalchooseLevel);
        textView = (TextView) findViewById(R.id.speechContent);
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener中onEvent方法，注册自己的事件输出类
        mHorVoiceView = (LineWaveVoiceView) findViewById(R.id.horvoiceview);
        tvRecordTips = (TextView) findViewById(R.id.record_tips);
        layoutCancelView = (LinearLayout) findViewById(R.id.pp_layout_cancel);
        recordAudioView = (ImageView) findViewById(R.id.iv_recording);
        recordStatusDescription = new String[]{
                getString(R.string.ar_feed_sound_press_record),
                getString(R.string.ar_feed_sound_slide_cancel)
        };
        recordAudioView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:   //按住的时候执行start
                        textView.setText("");
                        recordAudioView.setImageResource(R.drawable.ar_record_audio_pressed);
                        recordTotalTime = 0;
                        initTimer();
                        timer.schedule(timerTask,0,DEFAULT_MIN_TIME_UPDATE_TIME);
                        mHorVoiceView.setVisibility(View.VISIBLE);
                        mHorVoiceView.startRecord();
                        tvRecordTips.setVisibility(View.VISIBLE);
                        tvRecordTips.setText(recordStatusDescription[1]);
                        layoutCancelView.setVisibility(View.INVISIBLE);
                        start();
                        break;
                    case MotionEvent.ACTION_UP:
                        //mHorVoiceView.setVisibility(View.INVISIBLE);
                        recordAudioView.setImageResource(R.drawable.ar_record_audio_normal);
                        timer.cancel();
                        mHorVoiceView.setVisibility(View.INVISIBLE);
                        mHorVoiceView.stopRecord();
                        tvRecordTips.setVisibility(View.INVISIBLE);
                        layoutCancelView.setVisibility(View.VISIBLE);
                        showMyDialog(v);
                        //httpRequest(input);
                        stop();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        ivSet = (ImageView) findViewById(R.id.close_record);
        ivSet.setOnClickListener(this);
        emptyView = findViewById(R.id.audio_empty_layout);
        emptyView.setOnClickListener(this);
        mainHandler = new Handler();
    }
    /**
     * 初始化计时器用来更新倒计时
     */
    private void initTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //每隔1000毫秒更新一次ui
                        recordTotalTime += 1000;
                        updateTimerUI();
                    }
                });
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences("setting1", MODE_PRIVATE);
        hospitalchooseType = pref.getString("hospitalchooseType", "");
        hospitalchooseLevel = pref.getString("hospitalchooseLevel", "");
        docchooseLevel = pref.getString("docchooseLevel", "");
        distance = pref.getInt("distance", 100);
        distancePercent = pref.getInt("distancePercent", 50);
        levelPercent = pref.getInt("levelPercent", 50);
        Log.i("currentLocation", "读出来的"+lat+" "+distance+" "+levelPercent+
                " "+distancePercent+" "+hospitalchooseType+" "+hospitalchooseLevel);
    }

    private void updateTimerUI(){
        if(recordTotalTime >= maxRecordTime){
            //recordAudioView.invokeStop();
        } else{
            String string = String.format(" 倒计时 %s ", StringUtil.formatRecordTime(recordTotalTime,maxRecordTime));
            mHorVoiceView.setText(string);
            mHorVoiceView.setVoice();
            mHorVoiceView.setVoice();
            mHorVoiceView.setVoice();
            mHorVoiceView.setVoice();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.close_record){ //设置按钮
            Intent intent = new Intent(AudioRecordActivity.this, ChangeSetting.class);
            startActivity(intent);
        } else if(v.getId() == R.id.audio_empty_layout){
            onBackPressed();
        }
    }

    /**
     * 测试参数填在这里
     */
    private void start() {
        Toast.makeText(AudioRecordActivity.this, "请开始说话", Toast.LENGTH_SHORT).show();
        input = null;
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event
        //  if (enableOffline) {
        //  params.put(SpeechConstant.DECODER, 2);
        //  }
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
            input = resultTxt;  //获取字符串
            sendMessage(input);
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
            //Log.d(TAG, "Temp Params:"+result);
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
        //  txtLog.append(text + "\n");
    }

    public void showMyDialog(View v) {  //松开后就调用此方法开始转圈圈
     dialog =new MyProgressDialog(this, "正在识别中",R.drawable.loading);
        dialog.show();
        Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 20000);   //留一些加载时间
    }

    private void sendMessage(final String input) {    //将语音识别出的信息发送给服务器，第278行使用
        if(input != null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {    //这个不是子线程
                @Override
                public void run() {
                    //analyzeOption(input);
                    httpRequest(input);
                }
            }, 500);
        }
    }

    private void httpRequest(final String kd) { //提交输入结果给服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String kd1 = kd.replace(" ", "");
                    final String kd2 = kd1.replace("，", "");
                    Log.d("hos", kd2+lon+" "+lat+"==========");
                    OkHttpClient client = new OkHttpClient();
                    int idd = 1;
                    if(hospitalchooseLevel.equals("")) {
                        idd = 13;
                    } else if(hospitalchooseLevel.equals("三级甲等")) {
                        idd = 1;
                    }
                    RequestBody requestBody = new FormBody.Builder()
                            .add("kd", kd).build();
                    Log.i("currentLocation", "发送的"+lon+" "+lat+" "+distance+" "+levelPercent+
                            " "+distancePercent+" "+hospitalchooseType+" "+hospitalchooseLevel);
                    Request request = new Request.Builder().url(alihttp+"getSymptoms").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();     //获取服务器识别后得到的数据
                    Log.i("currentLocation", responseData);
                    JSONObject jsonObject = new JSONObject(responseData);
                    Boolean result = jsonObject.getBoolean("ifFind");
                    list.clear();
                    if(!result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {         //查询失败
                                dialog.dismiss();
                                Toast.makeText(AudioRecordActivity.this, "查询失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for(int i = 0; i < jsonArray.length(); i++) {
                            Symptom symptom =  new Symptom();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String disease = jsonObject1.getString("disease");
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("symptoms");
                            symptom.setDiseaseName(disease);
                            Log.i("currentLocation", disease);
                            for(int j = 0; j < jsonArray1.length(); j++) {
                                Log.i("currentLocation", jsonArray1.getString(j));
                                symptom.getSymptoms().add(jsonArray1.getString(j));
                            }
                            list.add(symptom);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(AudioRecordActivity.this, "查询成功",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AudioRecordActivity.this,
                                        OtherSymptoms.class);
                                intent.putExtra("hoslist", (Serializable) list);
                                intent.putExtra("kd", kd2);
                                intent.putExtra("lat", lat);
                                intent.putExtra("lon", lon);
                                startActivity(intent);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stop() {
        textView.append("");
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //发送停止录音事件
    }

    public void startLocaion() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation !=null ) {
                if (amapLocation.getErrorCode() == 0) {
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    lat = amapLocation.getLatitude();//获取纬度
                    lon = amapLocation.getLongitude();//获取经度

//                    lat = 30.626635;
//                    lon = 104.08221;
                    Log.i("currentLocation", "经纬度 "+ lat+" "+lon);
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };
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
