package comp5216.sydney.edu.au.keeprunning.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.DbException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import comp5216.sydney.edu.au.keeprunning.ProjectApp;
import comp5216.sydney.edu.au.keeprunning.R;
import comp5216.sydney.edu.au.keeprunning.model.RouteRecord;
import comp5216.sydney.edu.au.keeprunning.util.ToastUtils;
import comp5216.sydney.edu.au.keeprunning.util.Utils;

public class RunActivity extends AppCompatActivity implements SensorEventListener, OnClickListener {

    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private double lastX;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private boolean isStop = true;
    private boolean isPause = false;

    private boolean isFirstLoc = true; // 是否首次定位
    private boolean isFirstShow = true;
    private MyLocationData locData;
    private float mCurrentZoom = 18f;//默认地图缩放比例值
    private SensorManager mSensorManager;

    private Timer mTimer;
    private long startTime;
    private int costTime;
    private double totalDistance;

    //起点图标
    BitmapDescriptor startBD = BitmapDescriptorFactory.fromResource(R.mipmap.ic_start);
    //终点图标
    BitmapDescriptor finishBD = BitmapDescriptorFactory.fromResource(R.mipmap.ic_stop);

    List<LatLng> points = new ArrayList<LatLng>();//位置点集合
    Polyline mPolyline;//运动轨迹图层
    LatLng last = new LatLng(0, 0);//上一个定位点
    MapStatus.Builder builder;
    private TextView btn_back;
    private ImageButton btn_music;
    private Button btn_pause;
    private Button btn_continue;
    private TextView tv_time;
    private TextView tv_distance;
    private TextView tv_speed;
    private LinearLayout layout_run;
    private Button btn_start;
    private Button btn_stop;
    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        initView();
        initMap();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务

    }

    private void initView() {

        btn_back = (TextView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_music = (ImageButton) findViewById(R.id.btn_music);
        btn_music.setOnClickListener(this);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(this);
        btn_continue = (Button) findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(this);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        layout_run = (LinearLayout) findViewById(R.id.layout_run);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
    }

    private void initMap() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

        /**
         * 添加地图缩放状态变化监听，当手动放大或缩小地图时，拿到缩放后的比例，然后获取到下次定位，
         *  给地图重新设置缩放比例，否则地图会重新回到默认的mCurrentZoom缩放比例
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

            }
        });

        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//只用gps定位，需要在室外定位。
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];

        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;

            if (isFirstLoc) {
                lastX = x;
                return;
            }

            locData = new MyLocationData.Builder().accuracy(0)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (isStop) {
                    finish();
                } else {
                    ToastUtils.showToast(this, "Please stop first.");
                }
                break;
            case R.id.btn_music:

                startActivity(new Intent(this, MusicActivity.class));

                break;
            case R.id.btn_pause:
                isPause = true;
                btn_stop.setVisibility(View.GONE);
                btn_pause.setVisibility(View.GONE);
                btn_continue.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_continue:
                isPause = false;
                btn_stop.setVisibility(View.VISIBLE);
                btn_pause.setVisibility(View.VISIBLE);
                btn_continue.setVisibility(View.GONE);
                break;
            case R.id.btn_start:

                if (mLocClient != null && !mLocClient.isStarted()) {
                    startTime = System.currentTimeMillis();
                    costTime = 0;
                    totalDistance = 0;
                    btn_start.setVisibility(View.GONE);
                    btn_pause.setVisibility(View.VISIBLE);
                    btn_stop.setVisibility(View.VISIBLE);
                    mLocClient.start();
                    mBaiduMap.clear();
                    isStop = false;
                    layout_run.setVisibility(View.VISIBLE);
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }

                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            if (isPause) {
                                return;
                            }

                            costTime++;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_time.setText(Utils.secToTime(costTime));
                                }
                            });
                        }
                    }, 0, 1000);
                    tv_distance.setText("0.0");
                    tv_speed.setText("0");
                }

                break;
            case R.id.btn_stop:

                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }

                if (mLocClient != null && mLocClient.isStarted()) {
                    btn_start.setVisibility(View.VISIBLE);
                    btn_pause.setVisibility(View.GONE);
                    btn_stop.setVisibility(View.GONE);
                    isStop = true;
                    mLocClient.stop();

                    if (totalDistance > 0) {

                        MarkerOptions oFinish = new MarkerOptions();// 地图标记覆盖物参数配置类
                        oFinish.position(points.get(points.size() - 1));
                        oFinish.icon(finishBD);// 设置覆盖物图片
                        mBaiduMap.addOverlay(oFinish); // 在地图上添加此图层

                        RouteRecord routeRecord = new RouteRecord();
                        routeRecord.setCosttime(costTime);
                        routeRecord.setDatetime(System.currentTimeMillis());
                        routeRecord.setDistance(totalDistance);
                        routeRecord.setPoints(new Gson().toJson(points));

                        try {
                            ProjectApp.getInstance().getDb().saveBindingId(routeRecord);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtils.showToast(this, "The distance is too short to save.");
                    }

                    //复位
                    points.clear();
                    last = new LatLng(0, 0);
                    isFirstLoc = true;

                }

                break;
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {

            if (location == null || mMapView == null) {
                return;
            }

            if (isFirstShow) {
                mLocClient.stop();
                isFirstShow = false;

                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                //显示当前定位点，缩放地图
                locateAndZoom(location, ll);
            }

            if (isPause) {
                return;
            }


            if (isFirstLoc) {//首次定位
                //第一个点很重要，决定了轨迹的效果，gps刚开始返回的一些点精度不高，尽量选一个精度相对较高的起始点

                LatLng ll = getMostAccuracyLocation(location);
                if (ll == null) {
                    return;
                }
                isFirstLoc = false;
                points.add(ll);//加入集合
                last = ll;

                //显示当前定位点，缩放地图
                locateAndZoom(location, ll);

                //标记起点图层位置
                MarkerOptions oStart = new MarkerOptions();// 地图标记覆盖物参数配置类
                oStart.position(points.get(0));// 覆盖物位置点，第一个点为起点
                oStart.icon(startBD);// 设置覆盖物图片
                mBaiduMap.addOverlay(oStart); // 在地图上添加此图层

                return;//画轨迹最少得2个点，首地定位到这里就可以返回了
            }

            //从第二个点开始
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            //sdk回调gps位置的频率是1秒1个，位置点太近动态画在图上不是很明显，可以设置点之间距离大于为5米才添加到集合中
            double distantce = DistanceUtil.getDistance(last, ll);
            if (distantce < 2) {
                return;
            }

            totalDistance += distantce;
            tv_distance.setText(df.format(totalDistance / 1000));

            points.add(ll);//如果要运动完成后画整个轨迹，位置点都在这个集合中

            last = ll;

            float speed = location.getSpeed();
            speed = speed * 1000 / 3600;
            if (speed < 0) {
                speed = 0;
            }
            tv_speed.setText(df.format(speed));

            //显示当前定位点，缩放地图
            locateAndZoom(location, ll);

            //清除上一次轨迹，避免重叠绘画
            mMapView.getMap().clear();

            //起始点图层也会被清除，重新绘画
            MarkerOptions oStart = new MarkerOptions();
            oStart.position(points.get(0));
            oStart.icon(startBD);
            mBaiduMap.addOverlay(oStart);

            //将points集合中的点绘制轨迹线条图层，显示在地图上
            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(points);
            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        }

    }

    private void locateAndZoom(final BDLocation location, LatLng ll) {
        mCurrentLat = location.getLatitude();
        mCurrentLon = location.getLongitude();
        locData = new MyLocationData.Builder().accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);

        builder = new MapStatus.Builder();
        builder.target(ll).zoom(mCurrentZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 首次定位很重要，选一个精度相对较高的起始点
     * 注意：如果一直显示gps信号弱，说明过滤的标准过高了，
     * 你可以将location.getRadius()>25中的过滤半径调大，比如>40，
     * 并且将连续5个点之间的距离DistanceUtil.getDistance(last, ll ) > 5也调大一点，比如>10，
     * 这里不是固定死的，你可以根据你的需求调整，如果你的轨迹刚开始效果不是很好，你可以将半径调小，两点之间距离也调小，
     * gps的精度半径一般是10-50米
     */
    private LatLng getMostAccuracyLocation(BDLocation location) {

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        if (DistanceUtil.getDistance(last, ll) > 10) {
            last = ll;
            points.clear();//有任意连续两点位置大于10，重新取点
            return null;
        }
        points.add(ll);
        last = ll;
        //有5个连续的点之间的距离小于10，认为gps已稳定，以最新的点为起始点
        if (points.size() >= 5) {
            points.clear();
            return ll;
        }
        return null;
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
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        // 取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mLocClient.unRegisterLocationListener(myListener);
        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.stop();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.getMap().clear();
        mMapView.onDestroy();
        mMapView = null;
        startBD.recycle();
        finishBD.recycle();
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (isStop) {
                finish();
            } else {
                ToastUtils.showToast(this, "Please stop first.");
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
