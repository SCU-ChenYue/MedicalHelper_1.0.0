package com.example.medicalhlepers.ThreeMainFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.ChatRoom.ChatRoom;
import com.example.medicalhlepers.ChatRoom.Msg;
import com.example.medicalhlepers.DiagnoseOnline.Messages;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FragmentFind extends Fragment {
    private Context context;
    private List<Messages> list = new ArrayList<>();
    private List<PersonalMessageStore> personalMessageStores;
    private PersonalMessageStore personalMessageStore;
    private String userName;
    private List<Msg> msgList;
    private SwipeMenuListView listView;
    private SwipeRefreshLayout swipeRefresh;
    private App_Adapter adapter1;
    private WsListener wsListener;
    private WebSocket webSocket;
    private int CONNECT_TIMEOUT = 5000;
    private int FRAME_QUEUE_SIZE = 5;
    private Handler handler = new Handler() {
      public void handleMessage(Message msg) {
          switch (msg.what) {
              case 0:
                  adapter1.notifyDataSetChanged();
                  break;
          }
      }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        msgList = DataSupport.findAll(Msg.class);
        adapter1 = new App_Adapter();
        View contentView = inflater.inflate(R.layout.fragment_fragment_find, container, false);

        personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = personalMessageStores.get(0);
        userName = personalMessageStore.getUserName();
        context = contentView.getContext();
        initUnreadMsg();
        swipeRefresh = (SwipeRefreshLayout) contentView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(context);
                openItem.setBackground(new ColorDrawable(Color.GRAY));
                openItem.setWidth(dp2px(90));
                openItem.setTitle("打开");
                openItem.setTitleSize(20);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                deleteItem.setBackground(new ColorDrawable(Color.RED));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(20);
                menu.addMenuItem(deleteItem);
            }
        };
        listView = (SwipeMenuListView) contentView.findViewById(R.id.mySwipeList);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //index的值就是在SwipeMenu依次添加SwipeMenuItem顺序值，类似数组的下标。
                //从0开始，依次是：0、1、2、3...
                switch (index) {
                    case 0:
                        Messages messages1 = list.get(position);
                        Toast.makeText(context, "打开:" + position, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Messages messages2 = list.get(position);
                        for (int i = list.size() - 1; i >= 0; i--) {
                            if (list.get(i).getName().equals(messages2.getName())) {
                                DataSupport.deleteAll(Msg.class, "docName = ?", messages2.getName());
                                list.remove(i);
                            }
                        }
                        adapter1 = new App_Adapter();
                        listView.setAdapter(adapter1);
                        Toast.makeText(context, "删除:" + position, Toast.LENGTH_SHORT).show();
                        break;
                }
                // false : 当用户触发其他地方的屏幕时候，自动收起菜单。
                // true : 不改变已经打开菜单的样式，保持原样不收起。
                return false;
            }
        });
        // 监测用户在ListView的SwipeMenu侧滑事件。
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int pos) {
                Log.d("位置:" + pos, "开始侧滑...");
            }

            @Override
            public void onSwipeEnd(int pos) {
                Log.d("位置:" + pos, "侧滑结束.");
            }
        });
        //初始化数据集。
        initMessageList();
        adapter1 = new App_Adapter();
        listView.setAdapter(adapter1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Messages messages = list.get(position);
                String duifang = messages.getName();
                Intent intent = new Intent(getActivity(), ChatRoom.class);
                intent.putExtra("duifang", duifang);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUnreadMsg();
        msgList = DataSupport.findAll(Msg.class);   //将数据库中的聊天记录取出
        Collections.reverse(msgList);
        initMessageList();
        adapter1 = new App_Adapter();
        listView.setAdapter(adapter1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Messages messages = list.get(position);
                String duifang = messages.getName();
                Intent intent = new Intent(getActivity(), ChatRoom.class);
                intent.putExtra("duifang", duifang);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });
    }

    private void refreshFruits() {
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
                        initMessageList();
                        adapter1.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public int dp2px(float dipValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void initMessageList() {
        //Collections.reverse(msgList);
        for (int i = msgList.size() - 1; i >= 0; i--) {
            if (!msgList.get(i).getUserName().equals(userName)) {    //只留下当前该用户的消息记录
                msgList.remove(i);
            }
        }
        list.clear();
        Iterator<Msg> iterator = msgList.iterator();
        while (iterator.hasNext()) {
            int flag = 0;
            Messages messages = new Messages();
            Msg msg = (Msg) iterator.next();
            for (int i = 0; i < list.size(); i++) { //每次取出聊天记录，都遍历list中是否已经存在和该医生的聊天记录
                if (msg.getDocName().equals(list.get(i).getName())) {   //只显示最近接受到的消息
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                messages.setName(msg.getDocName());
                messages.setLastMsg(msg.getContent());
                messages.setTitle(msg.getTitle());
                messages.setUnreadMsg(msg.getMsgRead());
                list.add(messages);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //Fragment被隐藏时调用
            initUnreadMsg();
            msgList = DataSupport.findAll(Msg.class);
            Collections.reverse(msgList);
        } else {
            //Fragment显示时调用
            initUnreadMsg();
            msgList = DataSupport.findAll(Msg.class);
            Collections.reverse(msgList);
            initMessageList();
            adapter1 = new App_Adapter();
            listView.setAdapter(adapter1);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Messages messages = list.get(position);
                    String duifang = messages.getName();
                    Intent intent = new Intent(getActivity(), ChatRoom.class);
                    intent.putExtra("duifang", duifang);
                    intent.putExtra("userName", userName);
                    startActivity(intent);
                }
            });
        }
    }

    private void initUnreadMsg() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().
                detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        try {
            String http = "http://39.96.41.6:8080/demo/";
            String encoderString = URLEncoder.encode(userName, "utf-8");
            http = http.concat(encoderString);
            URL url = new URL(http);
            webSocket = new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT) //ws地址，和设置超时时间
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(wsListener = new WsListener())//添加回调监听
                    .connectAsynchronously();//异步连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class App_Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Messages getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.message_item, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Messages item = getItem(position);
            holder.doctorName.setText(item.getName());
            holder.lastMsg.setText(item.getLastMsg());
            holder.msgRead.setText(item.getUnreadMsg());
            return convertView;
        }

        class ViewHolder {
            TextView doctorName;
            TextView lastMsg;
            TextView msgRead;

            public ViewHolder(View convertView) {
                doctorName = (TextView) convertView.findViewById(R.id.textView21);
                lastMsg = (TextView) convertView.findViewById(R.id.textView23);
                msgRead = (TextView) convertView.findViewById(R.id.unreadmsg);
                convertView.setTag(this);
            }
        }
    }

    class WsListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            JSONObject jsonObject = new JSONObject(text);
            String message = jsonObject.getString("msg");
            String fromUser = jsonObject.getString("fromUser");
            System.out.println(fromUser + ": " + message);
            final Msg msg = new Msg(message, Msg.TYPE_RECEIVED, userName, fromUser, "", "未读");
            msg.save(); //存储该条聊天记录
            msgList = DataSupport.findAll(Msg.class);   //取出数据库中的聊天记录
            Collections.reverse(msgList);
            initMessageList();
            Message message1 = new Message();
            message1.what = 0;
            handler.sendMessage(message1);
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
}