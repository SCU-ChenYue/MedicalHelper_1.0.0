package com.example.medicalhlepers.ThreeOtherFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.ExamAdapter;
import com.example.medicalhlepers.PriceQuery.Examination;

import java.util.ArrayList;
import java.util.List;


public class Fragment1 extends Fragment {
    private List<Examination> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_fragment1, container, false);
        list.clear();
        initExamInfor();    //初始化检查项目表
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerview1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        ExamAdapter adapter = new ExamAdapter(list);
        recyclerView.setAdapter(adapter);
        return contentView;
    }

    private void initExamInfor() {
        Examination examination1 = new Examination("静脉曲张定位定压治疗",
                "1元/次","说明：辅料自费");
        list.add(examination1);
        Examination examination2 = new Examination("痔疮的日常处理",
                "5元/次", "说明：当天不能洗澡");
        list.add(examination2);
        Examination examination3 = new Examination("蛀牙的根管治疗",
                "10元/次", "说明：很痛的噢");
        list.add(examination3);
        Examination examination4 = new Examination("胃部B超",
                "40元/次", "说明：预计排队1小时");
        list.add(examination4);
        Examination examination5 = new Examination("血常规",
                "10元/次", "说明：空腹最佳");
        list.add(examination5);
        Examination examination6 = new Examination("全身核磁共振",
                "100/次", "说明：不可携带金属进入");
        list.add(examination6);
    }
}
