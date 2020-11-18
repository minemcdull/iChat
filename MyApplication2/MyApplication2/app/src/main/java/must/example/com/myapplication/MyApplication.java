package must.example.com.myapplication;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Lenovo on 2017/4/13.
 * */
public class MyApplication extends Application {

@Override

public void onCreate() {

//初始化百度地图SDK

SDKInitializer.initialize(getApplicationContext());

super.onCreate();

}

}