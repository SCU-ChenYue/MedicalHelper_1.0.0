package com.example.medicalhlepers.ThreeMainFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.example.medicalhelpers.R;


public class FragmentFind extends Fragment {

    private MapView mapView;
    private AMap aMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_find, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}

