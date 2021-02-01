package com.example.medicalhlepers.ChatRoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.DiagnoseOnline.OnlineDoc;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatRoom extends AppCompatActivity implements MsgAdapter.ItemInnerDeleteListener {
    private String userName;
    private List<Msg> msgList;
    private EditText inputText;
    private Button send;
    private ImageView edit;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private String content;
    private int CONNECT_TIMEOUT=5000;
    private int FRAME_QUEUE_SIZE=5;
    private WsListener wsListener;
    private WebSocket webSocket;
    private String duifang;
    private String status;
    private TextView textView_duifang;
    private TextView textView_status;;
    private String docName;
    private String title;
    private ImageView callPhone;
    private ImageView moreMsg;
    private String docPhone;
    private TextView unReadText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroom);
        Intent intent=getIntent();
        userName = intent.getStringExtra("userName");
        docName = intent.getStringExtra("duifang");
        LitePal.getDatabase();  //初始化数据库
        msgList = new ArrayList<>();
        msgList = DataSupport.where("userName = ? and docName = ?",
                userName, docName).find(Msg.class);
        Msg msg = new Msg();
        msg.setMsgRead("已读");
        msg.updateAll("userName = ? and docName = ?", userName, docName);
        initStatus();
        initPhone();
        duifang=intent.getStringExtra("duifang");
        status=intent.getStringExtra("status");
        textView_duifang = findViewById(R.id.chatroom_duifang);
        textView_status = findViewById(R.id.chatroom_textstatus);
        moreMsg = (ImageView) findViewById(R.id.daohanga);
        moreMsg.setOnClickListener(new View.OnClickListener() { //进入对方信息导航栏
            @Override
            public void onClick(View v) {

            }
        });
        unReadText = (TextView) findViewById(R.id.unreadmsg);
        textView_duifang.setText(duifang);
        callPhone = (ImageView) findViewById(R.id.calldoc);
        callPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ChatRoom.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatRoom.this, new
                            String[] {Manifest.permission.CALL_PHONE }, 1);
                } else {
                    call(docPhone);
                }
            }
        });

        inputText = findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        edit = (ImageView) findViewById(R.id.edit_up);
        msgRecyclerView = findViewById(R.id.msg_recycle_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().
                detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        try {
            String http="http://39.96.41.6:8080/demo/";
            String encoderString = URLEncoder.encode(userName,"utf-8");
            http=http.concat(encoderString);
            URL url=new URL(http);
            webSocket= new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT) //ws地址，和设置超时时间
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(wsListener = new WsListener())//添加回调监听
                    .connectAsynchronously();//异步连接
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        adapter.notifyItemInserted(msgList.size() - 1);
        msgRecyclerView.scrollToPosition(msgList.size() - 1);
        msgRecyclerView.setAdapter(adapter);
        adapter.setOnItemDeleteClickListener(this);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = inputText.getText().toString();
                if (!"".equals(content)){
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("msg", content);
                        jsonObject.put("toUser", duifang);
                        webSocket.sendText(jsonObject.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    inputText.setText("");
                    final Msg msg = new Msg(content,Msg.TYPE_SENT, userName, docName, title, "已读");
                    msg.save(); //存储该条聊天记录
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msgList.add(msg);
                            adapter.notifyItemInserted(msgList.size() - 1);
                            msgRecyclerView.scrollToPosition(msgList.size() - 1);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onItemInnerDeleteClick(int position) {
        Msg msg = msgList.get(position);
        Intent intent = new Intent(ChatRoom.this, OnlineDoc.class);
        intent.putExtra("userName", msg.getUserName());
        intent.putExtra("duifang", msg.getDocName());
        intent.putExtra("status", "");
        startActivity(intent);
    }


    private void call(String hosPhone) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+docPhone));
            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call(docPhone);
                } else {
                    Toast.makeText(this, "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void initPhone() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().add("name", docName).build();
                    Request request1 = new Request.Builder().url("http://39.96.41.6:8080/doctorInfo")
                            .post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request1).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    docPhone = jsonObject.getString("phone");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().build();
                    Request request1 = new Request.Builder().url("http://39.96.41.6:8080/doctorAll")
                            .post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request1).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    int j = jsonArray.length();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(jsonObject.getString("name").equals(docName)) {
                            status = jsonObject.getString("status");
                            title = jsonObject.getString("title");
                            break;
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(status.equals("离线")){
                                textView_status.setText("离线");
                            }
                            else {
                                textView_status.setText("在线");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class WsListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            JSONObject jsonObject=new JSONObject(text);
            String message=jsonObject.getString("msg");
            String fromUser=jsonObject.getString("fromUser");
            System.out.println(fromUser+": "+message);
            final Msg msg = new Msg(message, Msg.TYPE_RECEIVED, userName, docName, title, "已读");
            msg.save(); //存储该条聊天记录
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                }
            });
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
                throws Exception {
            super.onConnected(websocket, headers);
//            logger.info("连接成功");
            System.out.println("连接成功");
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception)
                throws Exception {
            super.onConnectError(websocket, exception);
//            logger.warning("连接错误：" + exception.getMessage());
            System.out.println("连接错误");
            Log.d("Socketexception", exception.toString());
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
                throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
//            logger.warning("断开连接");
            System.out.println("断开连接");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //webSocket.disconnect();
    }
}
