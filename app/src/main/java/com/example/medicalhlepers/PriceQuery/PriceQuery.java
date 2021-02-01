package com.example.medicalhlepers.PriceQuery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.medicalhelpers.R;
import com.example.medicalhlepers.Adapter.MyFragmentPagerAdapter;
import com.example.medicalhlepers.ThreeMainFragment.FragmentFind;
import com.example.medicalhlepers.ThreeMainFragment.FragmentHome;
import com.example.medicalhlepers.ThreeMainFragment.FragmentMessage;
import com.example.medicalhlepers.ThreeOtherFragment.Fragment1;
import com.example.medicalhlepers.ThreeOtherFragment.Fragment2;
import com.example.medicalhlepers.ThreeOtherFragment.Fragment3;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class PriceQuery extends AppCompatActivity {
    private TabLayout mTabLayout;
    private String [] mTitles = {  "检查费用","药品价格", "挂号费用"};
    private MyFragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_query);
        Intent intent = getIntent();
        mTabLayout = findViewById(R.id.tl_main);
        mViewPager = findViewById(R.id.vp_main);

        addTabToTabLayout();
        setupWithViewPager();
    }

    /**
     * Description：给TabLayout添加tab
     */
    private void addTabToTabLayout() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitles[i]));
        }
    }

    /**
     * Description：初始化FragmentPagerAdapter适配器并给ViewPager设置上该适配器，最后关联TabLayout和ViewPager
     */
    private void setupWithViewPager() {
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(new Fragment1());
        mFragments.add(new Fragment2());
        mFragments.add(new Fragment3());

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mAdapter.addTitlesAndFragments(mTitles, mFragments);

        mViewPager.setAdapter(mAdapter); // 给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager); //关联TabLayout和ViewPager
    }

}


