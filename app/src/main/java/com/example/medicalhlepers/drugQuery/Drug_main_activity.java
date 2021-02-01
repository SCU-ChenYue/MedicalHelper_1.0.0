package com.example.medicalhlepers.drugQuery;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.medicalhelpers.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Drug_main_activity extends AppCompatActivity {
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private Fragment firstFragment=new DrugQueryMain();
    private Fragment secondFragment=new Fragment_drug_shop();
    private void setDefaultFragment(){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container_drug,firstFragment);
        transaction.add(R.id.container_drug,secondFragment);
        transaction.show(firstFragment).hide(secondFragment).commit();
//        transaction.replace(R.id.container_drug, new DrugQueryMain());
//        transaction.commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    transaction.hide(secondFragment).show(firstFragment).commit();
//                    transaction.replace(R.id.container_drug,new DrugQueryMain());
//                    transaction.commit();
                    return true;
                case R.id.navigation_shop:
                    transaction.hide(firstFragment).show(secondFragment).commit();
//                    transaction.replace(R.id.container_drug,new Fragment_drug_shop());
//                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view_drug);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setDefaultFragment();
    }
}
