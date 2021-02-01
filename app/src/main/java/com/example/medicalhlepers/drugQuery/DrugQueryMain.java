package com.example.medicalhlepers.drugQuery;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.DrugAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DrugQueryMain extends Fragment implements TextWatcher {
    private View contentView;
    private List<Drug_item> DrugList=new ArrayList<Drug_item>();
    private EditText editText;
    private TextView searchView;
    private DrugAdapter adapter;
    private ImageView deleteView;
    private String convertKD="";//当前查询关键字
    private boolean ifCanLoadMore=true;//是否还可以加载更多
    private static final String alihttp="http://39.96.41.6:8080/";
    private SmartRefreshLayout mRefreshLayout;
    private LinearLayout getBack;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.drugquery_main, container, false);
        mRefreshLayout=contentView.findViewById(R.id.drug_refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                String input =editText.getText().toString();
                convertKD=input;
                getListFromHTTPs(convertKD);
                mRefreshLayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getMoreItem();
                mRefreshLayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败
            }
        });
        initDrugs();
        RecyclerView recyclerView=(RecyclerView) contentView.findViewById(R.id.drug_listview);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DrugAdapter(DrugList);
        recyclerView.setAdapter(adapter);
        editText = (EditText) contentView.findViewById(R.id.drug_search_edit);
        editText.addTextChangedListener(this);
        deleteView = (ImageView) contentView.findViewById(R.id.ivDeleteText);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        searchView=(TextView) contentView.findViewById(R.id.searchbutton);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                switch (v.getId()){
                    case R.id.searchbutton:
                        String inputText=editText.getText().toString();
                        getListFromHTTPs(inputText);
                }
            }

        });
        getBack = (LinearLayout) contentView.findViewById(R.id.header_layout_lift);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return contentView;
    }
    private void initDrugs() {
       getListFromHTTP("");
       }
    private void getListFromHTTP(final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("kd", convertKD)
                            .add("num",""+0).build();
                    Request request = new Request.Builder().url(alihttp+"getDrugList").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    List<Drug_item> tolist=parseJSONWithJSONObject(responseData);
                    showInUI(tolist);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getListFromHTTPs(final String kd){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("kd", kd)
                            .add("num",""+0).build();
                    Request request = new Request.Builder().url(alihttp+"getDrugList").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    List<Drug_item> tolist=parseJSONWithJSONObject(responseData);
                    showInUI(tolist);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showInUI(final List<Drug_item> tolist){
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DrugList=tolist;
                RecyclerView recyclerView=(RecyclerView) contentView.findViewById(R.id.drug_listview);
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new DrugAdapter(DrugList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private List<Drug_item> parseJSONWithJSONObject(String jsonData){
        List<Drug_item> tolist=new ArrayList<Drug_item>();
        try {
            JSONArray jsonArray=new JSONArray(jsonData);
            if(jsonArray.length()<20)
                ifCanLoadMore=false;
            else ifCanLoadMore=true;
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                Drug_item drug_item = new Drug_item();
                drug_item.setName(jsonObject.getString("drugName"));
                drug_item.setDes(jsonObject.getString("description"));
                drug_item.setImageUrl(jsonObject.getString("imageUrl"));
                drug_item.setPrice(jsonObject.getDouble("price"));
                drug_item.setType(jsonObject.getString("type"));
                drug_item.setSpecs(jsonObject.getString("specification"));
                tolist.add(drug_item);
            }
        }catch (Exception e){
            System.out.println("parseJSONWithJSONObject ERROR");
        }
        return tolist;
    }
    public void login(final String name,final String passed){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("name", name)
                            .add("passwd",passed).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/login").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONArray jsonArray=new JSONArray(responseData);
                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                    String result=jsonObject.getString("result");
                    System.out.println(result);
                    if (result.equals("登录失败")){    //登录失败

                    }
                    else{                      //登陆成功

                    }
                    //注意，网络访问必须在子线程中进行，要更改UI界面必须回到主线程，方法自己百度。。。
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getMoreItem(){
        //获取更多药物
        if(ifCanLoadMore) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int convertNum = DrugList.size();
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder().add("kd", convertKD)
                                .add("num", "" + convertNum).build();
                        Request request = new Request.Builder().url(alihttp + "getDrugList").post(requestBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        org.json.JSONArray jsonArray = new org.json.JSONArray(responseData);
                        if(jsonArray.length()<20)
                            ifCanLoadMore=false;
                        else ifCanLoadMore=true;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Drug_item drug_item = new Drug_item();
                            drug_item.setName(jsonObject.getString("drugName"));
                            drug_item.setDes(jsonObject.getString("description"));
                            drug_item.setImageUrl(jsonObject.getString("imageUrl"));
                            drug_item.setPrice(jsonObject.getDouble("price"));
                            drug_item.setType(jsonObject.getString("type"));
                            drug_item.setSpecs(jsonObject.getString("specification"));
                            DrugList.add(drug_item);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }//可以加载更多
        else
        {
            Toast.makeText(getContext(),"没有更多的数据了",Toast.LENGTH_SHORT).show();
        }//不能加载更多
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(editText.getText().toString().equals("")) {  //如果是空的
            getListFromHTTP("");
            adapter.notifyDataSetChanged();
        } else {
            String inputText=editText.getText().toString();
            getListFromHTTPs(inputText);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

}
