package com.example.medicalhlepers.drugQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public   class loginAdapter {
    public static void main(String[] args) {
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
                    if (result=="登录失败"){    //登录失败
                        System.out.println("失败");
                    }
                    else{                      //登陆成功
                        System.out.println("成功");
                    }
                    //注意，网络访问必须在子线程中进行，要更改UI界面必须回到主线程，方法自己百度。。。
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
