package com.example.medicalhlepers.ThreeMainFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.BasicActivity.ChangePassword;
import com.example.medicalhlepers.Forum.AskQuestion;
import com.example.medicalhlepers.Forum.MyCollector;
import com.example.medicalhlepers.Forum.MyQuestion;
import com.example.medicalhlepers.Login.Login;
import com.example.medicalhlepers.PersonalInformation.PersonalInformation;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;
import com.example.medicalhlepers.Tools.CloseDialog;
import com.example.medicalhlepers.Yuyue.AppointmentRecords;

import org.litepal.crud.DataSupport;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentHome extends Fragment {
    private CircleImageView imageViewMessage;
    private TextView userNameText;
    private LinearLayout changePassword;
    private LinearLayout exitThis;
    private LinearLayout yuyueRecord;
    private PersonalMessageStore personalMessageStore;
    private List<PersonalMessageStore> personalMessageStoreList;
    private CloseDialog closeDialog;
    private LinearLayout myQuestion;
    private LinearLayout myCollect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        imageViewMessage = (CircleImageView) contentView.findViewById(R.id.imageView4);
        imageViewMessage.setOnClickListener(new View.OnClickListener() {    //点击头像修改个人资料
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonalInformation.class);
                startActivity(intent);
            }
        });
        //PersonalMessageStore personalMessageStore = new PersonalMessageStore();
        //personalMessageStore.save();
        userNameText = (TextView) contentView.findViewById(R.id.username_text);
        personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = personalMessageStoreList.get(0);
        userNameText.setText(personalMessageStore.getUserName());
        String a = personalMessageStore.getUserName();
        //修改密码
        changePassword = (LinearLayout) contentView.findViewById(R.id.xiugaimima);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                startActivity(intent);
            }
        });
        //退出登录
        closeDialog = new CloseDialog(getActivity());
        closeDialog.setYesOnclickListener("确定", new CloseDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {  //点击了确定按钮
                closeDialog.dismiss();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        closeDialog.setNoOnclickListener("取消", new CloseDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                closeDialog.dismiss();
            }
        });
        exitThis = (LinearLayout) contentView.findViewById(R.id.resiglogin);
        exitThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog.show();
            }
        });
        //查看挂号记录
        yuyueRecord = (LinearLayout) contentView.findViewById(R.id.yuyue);
        yuyueRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AppointmentRecords.class);
                startActivity(intent);
            }
        });
        //我的问题
        myQuestion = (LinearLayout) contentView.findViewById(R.id.dangan);
        myQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyQuestion.class);
                startActivity(intent);
            }
        });
        //我的收藏
        myCollect = (LinearLayout) contentView.findViewById(R.id.jiuyika);
        myCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCollector.class);
                startActivity(intent);
            }
        });
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        personalMessageStoreList = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = personalMessageStoreList.get(0);
        userNameText.setText(personalMessageStore.getUserName());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //从服务器获取当前用户的用户信息

}