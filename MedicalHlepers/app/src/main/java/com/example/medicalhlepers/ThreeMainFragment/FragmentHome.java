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
import com.example.medicalhlepers.Login.Login;
import com.example.medicalhlepers.PersonalInformation.PersonalInformation;
import com.example.medicalhlepers.PersonalInformation.PersonalMessageStore;

import org.litepal.crud.DataSupport;

import java.util.List;

public class FragmentHome extends Fragment {
    private ImageView imageViewMessage;
    private TextView userNameText;
    private LinearLayout changePassword;
    private LinearLayout exitThis;
    private PersonalMessageStore personalMessageStore;
    private List<PersonalMessageStore> personalMessageStoreList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        imageViewMessage = (ImageView) contentView.findViewById(R.id.imageView4);
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
        exitThis = (LinearLayout) contentView.findViewById(R.id.resiglogin);
        exitThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
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