package com.example.medicalhlepers.Test;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import com.example.medicalhelpers.R;
import java.util.zip.Inflater;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.medicalhelpers.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author angela
 */
public class Test extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        showCustomDialog();
    }


    public void showCustomDialog(){
        final Dialog mDialog = new Dialog(this, R.style.CustomDialogTheme);
        //获取要填充的布局
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.send_tel_dailog,null);
        //设置自定义的dialog布局
        mDialog.setContentView(v);
        //false表示点击对话框以外的区域对话框不消失，true则相反
        mDialog.setCanceledOnTouchOutside(false);
        final WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = 800;
        mDialog.getWindow().setAttributes(params);
        mDialog.show();
        //获取自定义dialog布局控件
        Button shenfenzhengBt = (Button)v.findViewById(R.id.shenfenzheng);
        Button hukoubuBt = (Button) v.findViewById(R.id.hukoubu);
        Button zhongguoBt = (Button) v.findViewById(R.id.zhongguohuzhao);
        Button waiguoBt = (Button) v.findViewById(R.id.waiguohuzhao);
        Button gangaoBt = (Button) v.findViewById(R.id.gangaotongxing);
        Button taiwanBt = (Button) v.findViewById(R.id.taiwan);
        Button shibingBt = (Button) v.findViewById(R.id.shibingzheng);
        //身份证
        shenfenzhengBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
        // TODO Auto-generated method stub

                mDialog.dismiss();
            }
        });
        //户口簿
        hukoubuBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
        // TODO Auto-generated method stub

                mDialog.dismiss();
            }
        });
        //中国护照
        zhongguoBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });
        //外国护照
        waiguoBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });
        //港澳通行证
        gangaoBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });
        //台湾通行证
        taiwanBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });
        //士兵证
        shibingBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });
    }
}

