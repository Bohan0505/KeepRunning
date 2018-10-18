package comp5216.sydney.edu.au.keeprunning.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.keeprunning.R;
import comp5216.sydney.edu.au.keeprunning.model.RouteRecord;
import comp5216.sydney.edu.au.keeprunning.util.Utils;

public class DetailActivity extends AppCompatActivity {

    // 定位相关
    MapView mMapView;
    BaiduMap mBaiduMap;
    Polyline mPolyline;
    LatLng target;
    MapStatus.Builder builder;
    List<LatLng> latLngs = new ArrayList<LatLng>();

    //起点图标
    BitmapDescriptor startBD = BitmapDescriptorFactory.fromResource(R.mipmap.ic_start);
    //终点图标
    BitmapDescriptor finishBD = BitmapDescriptorFactory.fromResource(R.mipmap.ic_stop);

    private Marker mMarkerA;
    private Marker mMarkerB;
    private InfoWindow mInfoWindow;
    private RouteRecord mRecord;
    private TextView btn_back;
    private TextView tv_time;
    private TextView tv_distance;
    private TextView tv_speed;
    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();

        mRecord = (RouteRecord) getIntent().getSerializableExtra("data");

        tv_time.setText(Utils.secToTime(mRecord.getCosttime()));
        tv_speed.setText(df.format(mRecord.getDistance() / mRecord.getCosttime()));
        tv_distance.setText(df.format(mRecord.getDistance() / 1000));

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        coordinateConvert();

        if (latLngs.size() == 0) {
            return;
        }

        builder = new MapStatus.Builder();
        builder.target(target).zoom(18f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        MarkerOptions oStart = new MarkerOptions();//地图标记覆盖物参数配置类
        oStart.position(latLngs.get(0));//覆盖物位置点，第一个点为起点
        oStart.icon(startBD);//设置覆盖物图片
        oStart.zIndex(1);//设置覆盖物Index
        mMarkerA = (Marker) (mBaiduMap.addOverlay(oStart)); //在地图上添加此图层

        //添加终点图层
        MarkerOptions oFinish = new MarkerOptions().position(latLngs.get(latLngs.size() - 1)).icon(finishBD).zIndex(2);
        mMarkerB = (Marker) (mBaiduMap.addOverlay(oFinish));

        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(latLngs);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        mPolyline.setZIndex(3);
    }

    /**
     * 讲google地图的wgs84坐标转化为百度地图坐标
     */
    private void coordinateConvert() {

        if (mRecord == null) {
            return;
        }

        List<LatLng> routePoints = new Gson().fromJson(mRecord.getPoints(), new TypeToken<List<LatLng>>() {
        }.getType());

        double lanSum = 0;
        double lonSum = 0;
        for (LatLng ll : routePoints) {
            latLngs.add(ll);
            lanSum += ll.latitude;
            lonSum += ll.longitude;
        }
        target = new LatLng(lanSum / latLngs.size(), lonSum / latLngs.size());
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        // 为系统的方向传感器注册监听器
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.getMap().clear();
        mMapView.onDestroy();
        mMapView = null;
        startBD.recycle();
        finishBD.recycle();
        super.onDestroy();
    }

    private void initView() {
        btn_back = (TextView) findViewById(R.id.btn_back);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_speed = (TextView) findViewById(R.id.tv_speed);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
