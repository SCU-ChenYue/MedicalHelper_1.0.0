package com.example.medicalhlepers.MapUse;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.HospitalAddress.HosAddre;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MapUse extends AppCompatActivity {
    private MapView mapView;
    private AMap aMap;
    private String hosName;
    private List<HosAddre> addreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        hosName = intent.getStringExtra("hosName");
        Toast.makeText(MapUse.this, hosName, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_map_use);
        mapView = (MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        if(aMap ==null){
            aMap = mapView.getMap();
        }
        //AMap aMap = mMapView.getMap();
        List<HosAddre> hosAddres = DataSupport.findAll(HosAddre.class);
        double longitude = 0;
        double latitude = 0;
        for(HosAddre hosAddre1: hosAddres) {
            if(hosAddre1.getHosName().equals(hosName)) {
                longitude = hosAddre1.getLongitude();
                latitude = hosAddre1.getLatitude();
                break;
            }
        }
        LatLng latLng = new LatLng(longitude, latitude);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));

        //设置远小近大效果,设置刷新一次图片资源的周期。
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title("北京市").snippet("北京市：39.5427, 116.2317");
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
        aMap.addMarker(markerOption);
    }


    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }
}
