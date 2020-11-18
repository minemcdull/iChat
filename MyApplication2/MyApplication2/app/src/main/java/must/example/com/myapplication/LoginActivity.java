package must.example.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import static java.security.AccessController.getContext;

/**
 * Created by Lenovo on 2017/4/10.
 */

public class LoginActivity extends Activity
{

    private EditText editac;
    private EditText editps;
    private Button login;

    private Button register;

    private String ServerIp="172.16.0.18";

    private int REGISTER=1;
    private int LOGIN=2;
    private int OFFLINE=3;

    public static String username;
    private int resourceId;

    private void init()
    {
        editac=(EditText)findViewById(R.id.account);
        editps=(EditText)findViewById(R.id.password);
        editps.setTransformationMethod(PasswordTransformationMethod.getInstance());
        login=(Button)findViewById(R.id.ac);
        register=(Button)findViewById(R.id.ps);
    }
    //获取本机MAC信息
    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    private String getlocalip() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        Log.e("TAG", "int ip " + ipAddress);
        if (ipAddress == 0) return null;
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login);
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

            init();
            //登录事件
            login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                try
                {
                    //一个写死的服务器ip
                    Socket socket=new Socket(ServerIp,9006);
                    String account = editac.getText().toString();
                    LoginActivity.username=account;
                    String password=editps.getText().toString();

                    //发送验证数据到服务器
                    PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
                    out.println(LOGIN+"&"+account+"&"+password+"&"+getlocalip());
                    Log.e("MP","tcp socket success");

                    // 获取服务器验证数据
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = br.readLine();

                    out.close();
                    br.close();
                    socket.close();

                    if ( msg.equals("false") )
                    {
                        Toast.makeText(LoginActivity.this,"Error Login Info",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        //此处写密码验证通过后转入MainActivity的逻辑
                        Log.e("MP",msg);
                        String []ip=msg.split("&");
                        if(ip[1].equals("0"))
                        {
                            Toast.makeText(LoginActivity.this,"friend offline",Toast.LENGTH_LONG).show();
                            MainActivity.isOnline=false;

                            //好友不在线转入离线发到服务器的Activity
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);

                            //获取离线消息的长度
                            intent.putExtra("msglength",(ip.length-2));
                            for(int i=2;i<ip.length;i++)
                            {
                                Log.e("MP"+i,ip[i]);
                                intent.putExtra("key"+(i-2),ip[i]);
                            }
                            intent.putExtra("ip",ip[1]);
                            startActivity(intent);
                        }
                        else {
                            //好友在线  获取返回的ip地址
                            MainActivity.isOnline=true;
                            String [] ip1=msg.split("&");
                            Log.e("MP",ip1[1]);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            //获取离线消息的长度
                            intent.putExtra("msglength",(ip.length-2));
                            for(int i=2;i<ip.length;i++)
                            {
                                Log.e("MP"+i,ip[i]);
                                intent.putExtra("key"+(i-2), ip[i]);
                            }
                            intent.putExtra("ip", ip[1]);
                            startActivity(intent);
                        }
                    }


                }
                catch(Exception e)
                {
                    Log.e("MP","tcp socket error");
                    e.printStackTrace();
                }
            }
            });

            // 注册事件
            register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                try
                {
                    //一个写死的服务器ip
                    Socket socket=new Socket(ServerIp,9006);
                    Log.e("MP1","tcp socket success1");
                    String account = editac.getText().toString();
                    String password=editps.getText().toString();

                    //发送验证数据到服务器
                    PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
                    out.println(REGISTER+"&"+account+"&"+password+"&"+getlocalip());


                    // 获取服务器验证数据
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = br.readLine();

                    if ( msg != null )
                    {
                        //此处写注册成功后转入MainActivity的逻辑
                        Toast.makeText(LoginActivity.this,"Register Success Please Login",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Error Register Info",Toast.LENGTH_LONG).show();
                    }

                    out.close();
                    br.close();
                    socket.close();
                }
                catch(Exception e)
                {
                    Log.e("MP","tcp socket error1");
                    e.printStackTrace();
                }
            }
        });

    }

}
