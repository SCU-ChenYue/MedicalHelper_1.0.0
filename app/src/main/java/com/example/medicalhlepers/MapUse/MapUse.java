package com.example.medicalhlepers.MapUse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.services.search.model.LatLonPoint;
import com.example.medicalhelpers.R;
import com.example.medicalhlepers.BasicActivity.MainActivity;
import com.example.medicalhlepers.HospitalAddress.HosAddre;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import java.util.List;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapUse extends AppCompatActivity{
    private MapView mapView;
    private AMap aMap;
    private String hosName;
    private String hosAddre;
    private double longitude;
    private double latitude;
    private double lat; //用户的
    private double lon;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    private String TAG;
    private TextView hospitalName;
    private TextView hosAddress;
    private TextView hosDistance;
    private Button routeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_use);
        //startLocaion();
        Intent intent = getIntent();
        hosName = intent.getStringExtra("hosName");
        hosAddre = intent.getStringExtra("hosAddre");
        if(ContextCompat.checkSelfPermission(MapUse.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MapUse.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
        }else{
            //开始定位
            startLocaion();
            Toast.makeText(MapUse.this,"已开启定位权限",Toast.LENGTH_LONG).show();
        }

        hospitalName = (TextView) findViewById(R.id.hospitalName);
        hosAddress = (TextView) findViewById(R.id.hospitalAddre);
        hosDistance = (TextView) findViewById(R.id.hospitalDistance);
        routeButton = (Button) findViewById(R.id.checkroute);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation(getApplicationContext(), lat, lon, latitude, longitude);
            }
        });
        Toast.makeText(MapUse.this, hosName, Toast.LENGTH_SHORT).show();
        mapView = (MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        hospitalName.setText(hosName);
        hosAddress.setText(hosAddre);
        initLocation();
        //init();
        //navigation(this, lat, lon, latitude, longitude);
    }


    private void initLocation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder().
                            add("hosName", hosName).build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/hosLocation").
                            post(requestBody)
                            .build();
                    //获取数据
                    Response response = client.newCall(request).execute();
                    String responseData=response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    longitude = jsonObject.getDouble("longitude");
                    latitude = jsonObject.getDouble("latitude");
                    Log.i("currentLocation", "医院" + latitude + " " + longitude);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            init();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void init() {
        if(aMap ==null) {
            aMap = mapView.getMap();
        }
        //AMap aMap = mMapView.getMap();
        LatLng latLng = new LatLng(latitude, longitude);
        Log.i("currentLocation", "医院" + latitude + " " + longitude);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        //设置远小近大效果,设置刷新一次图片资源的周期。
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title("北京市").snippet("北京市：39.5427, 116.2317");
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
        aMap.addMarker(markerOption);
        //navigation(this, lat, lon, latitude, longitude);
    }

    public void startLocaion() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation !=null ) {
                if (amapLocation.getErrorCode() == 0) {
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    double currentLat = amapLocation.getLatitude();//获取纬度
                    double currentLon = amapLocation.getLongitude();//获取经度
                    //Log.i("currentLocation", "currentLat : " + currentLat + " currentLon : " + currentLon);
                    lat = amapLocation.getLatitude();
                    lon = amapLocation.getLongitude();
                    Log.i("currentLocation", "我的经纬度 "+ lat+" "+lon);
                    Log.i("currentLocation", "医院的经纬度" + latitude + " " + longitude);
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.i("currentLocation", "======== ");
                    Log.e("currentLocation", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    /**
     * 路线规划
     *
     * @param slat 起点纬度
     * @param slon 起点经度
     * @param dlat 终点纬度
     * @param dlon 终点经度
     */
    public void navigation(Context context, double slat, double slon, double dlat, double dlon) {
        Poi start = null;
        //如果设置了起点
        if (slat != 0 && slon != 0) {
            start = new Poi("我的位置", new LatLng(slat, slon), "");
        }
        Poi end = new Poi(hosName, new LatLng(dlat, dlon), "");
        AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER);
        params.setUseInnerVoice(true);
        params.setMultipleRouteNaviMode(true);
        params.setNeedDestroyDriveManagerInstanceWhenNaviExit(true);
        //发起导航
        AmapNaviPage.getInstance().showRouteActivity(context, params, null);
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
        Log.d(TAG, "经纬度"+lat+" "+lon);
        if(mLocationClient!=null) {
            mLocationClient.stopLocation();//停止定位
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mLocationClient!=null) {
            mLocationClient.startLocation(); // 启动定位
        }
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
        if(mLocationClient!=null) {
            mLocationClient.onDestroy();//销毁定位客户端。
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200://刚才的识别码
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){//用户同意权限,执行我们的操作
                    startLocaion();//开始定位
                }else{//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                    Toast.makeText(MapUse.this,"未开启定位权限,请手动到设置去开启权限",Toast.LENGTH_LONG).show();
                }
                break;
            default:break;
        }
    }
}
