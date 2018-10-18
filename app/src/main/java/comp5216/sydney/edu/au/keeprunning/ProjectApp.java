package comp5216.sydney.edu.au.keeprunning;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import comp5216.sydney.edu.au.keeprunning.model.RouteRecord;

public class ProjectApp extends Application {

    private DbUtils db;
    private static ProjectApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    public static ProjectApp getInstance() {
        return mInstance;
    }

    //数据库初始化
    public DbUtils getDb() {
        if (db == null) {
            DbUtils.DaoConfig daoConfig = new DbUtils.DaoConfig(this);
            daoConfig.setDbName("data.db");
            daoConfig.setDbVersion(1);
            db = DbUtils.create(daoConfig);
            try {
                db.createTableIfNotExist(RouteRecord.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        return db;
    }

}