package must.example.com.myapplication;

/**
 * Created by Lenovo on 2017/4/13.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity{
    private Button button;
    private TextView text;
    private String provider;
    private MapView map;
    private BaiduMap baiduMap;
    private boolean isFirst=true;

    private Socket socket=null;
    private String ServerIP="172.16.0.18";
    private final int SEND=0;
    @Override
    protected void onDestroy()
    {
        Log.e("onDestroy","erroe");
        map.onDestroy();

        super.onDestroy();
    }
    private void showLocation(final Location location)
    {
        final double j=location.getLongitude();
        final double w=location.getLatitude();
        text.setText("longtitude:"+j+'\n'+"latitude:"+w);

        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("thx baidu");
        dialog.setMessage("gain location succeed");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                map=(MapView)findViewById(R.id.map);
                MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
                locationBuilder.latitude(w);
                locationBuilder.longitude(j);
                MyLocationData locationData=locationBuilder.build();
                if(isFirst)
                {
                    baiduMap=map.getMap();
                    LatLng ll=new LatLng(w,j);
                    MapStatusUpdate updata=MapStatusUpdateFactory.newLatLng(ll);
                    baiduMap.animateMapStatus(updata);


                    isFirst=false;
                }
                //updata=MapStatusUpdateFactory.zoomTo(16f);
                //baiduMap.animateMapStatus(updata);
                baiduMap.setMyLocationEnabled(true);
                baiduMap.setMyLocationData(locationData);


            }
        });
        dialog.setNegativeButton("No",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub

            }
        });
        dialog.show();

        //upload location
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try {
                    socket = new Socket(ServerIP, 9006);
                    //String content = inputText.getText().toString();
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    out.println("5&"+LoginActivity.username + "&" + j + "&" + w +"&");
                    out.flush();


                    Log.e("MP", "tcp socket success");
                    /*
                    Message message = new Message();
                    message.what = SEND;
                    message.obj = content;
                    new SendBuf(content);
                    */
                    //inputText.setText("");
                    // 获取服务器验证数据
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = br.readLine();
                    out.close();
                    socket.close();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Log.e("MP","Offline upload error");
                }
            }



        }).start();
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.mapview);
        button=(Button)findViewById(R.id.button);
        text=(TextView)findViewById(R.id.text);
        map=(MapView)findViewById(R.id.map);

        button.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);

                List<String> providerList=locationManager.getProviders(true);
                if(providerList.contains(LocationManager.NETWORK_PROVIDER))
                {
                    provider=LocationManager.NETWORK_PROVIDER;;

                }
                else if(providerList.contains(LocationManager.GPS_PROVIDER))
                {
                    provider=LocationManager.GPS_PROVIDER;
                }
                else
                {
                    Toast.makeText(Main.this,"GPS is invalid",Toast.LENGTH_SHORT).show();
                    return;
                }


                Location location=locationManager.getLastKnownLocation(provider);
                if(location !=null)
                {
                    showLocation(location);
                }
                else
                {
                    Log.e("Location","NULL");
                }
                LocationListener listener=new LocationListener(){

                    @Override
                    public void onLocationChanged(Location arg0) {
                        // TODO Auto-generated method stub
                        //showLocation(arg0);
                        Log.e("onLocationChange","location change");

                    }

                    @Override
                    public void onProviderDisabled(String arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProviderEnabled(String arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                        // TODO Auto-generated method stub

                    }

                };
                locationManager.requestLocationUpdates(provider,5000,1000,listener);
            }

        });


    }

}
