package must.example.com.myapplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    //定义Socket 变量
    private static DatagramSocket Socket_2;
    private static InetAddress ip_1,ip_2;
    private static int port=8090;

    // 定义 组件变量
    private ListView msgListView;

    private EditText inputText;

    private ImageButton upload;

    private Button msgsend;

    private ImageButton loc;

    private MsgAdapter adapter;

    private List<Msg> msgList=new ArrayList<Msg>();

    private final int SEND=0;

    private final int RECEIVE=1;

    private TextView text;

    //在线判断标志
    public static boolean isOnline=true;

    private Socket socket=null;

    private String ServerIP="172.16.0.18";


    //子线程返回数据更新UI
    private Handler handler =new Handler(){
        public void handleMessage(Message msg)
        {
            if(msg.what==SEND)
            {
                //text.setText(msg.obj.toString());

                Msg msgget=new Msg(SendBuf.Get(),Msg.TYPE_SENT);
                msgList.add(msgget);
                // 当有新消息  刷新ListView中的显示
                adapter.notifyDataSetChanged();
                //将ListView定位到最后一行
                msgListView.setSelection(msgList.size());
                inputText.setText("");
            }
            if(msg.what==RECEIVE)
            {
                Msg msgget=new Msg(OffReceive.Get(),Msg.TYPE_RECEIVED);
                msgList.add(msgget);
                // 当有新消息  刷新ListView中的显示
                adapter.notifyDataSetChanged();
                //将ListView定位到最后一行
                msgListView.setSelection(msgList.size());
            }
        }

    };

    private String getlocalip(){
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        Log.e("TAG", "int ip "+ipAddress);
        if(ipAddress==0)return null;
        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        // 发送按钮的逻辑
        adapter=new MsgAdapter(MainActivity.this,R.layout.msg_item,msgList);
        text=(TextView)findViewById(R.id.text);
        inputText=(EditText)findViewById(R.id.input_text);
        msgsend=(Button)findViewById(R.id.send);
        upload=(ImageButton)findViewById(R.id.upload);
        loc=(ImageButton) findViewById(R.id.loc);

        msgListView=(ListView)findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);


        //上传图像的逻辑
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,ImageHandle.class);
                startActivity(intent);

            }

        });
        // 定位服务逻辑
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,Main.class);
                startActivity(intent);

            }

        });

        //获取登录状态返回的数据
        final String ip;
        final Intent intent=getIntent();
        final int length=intent.getIntExtra("msglength",0);
        Log.e("MPintent",length+"length");
        ip=intent.getStringExtra("ip");
        Log.e("MPintent",ip);
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                for(int i=0;i<length;i++)
                {
                    Message message = new Message();
                    message.what = RECEIVE;
                    message.obj = intent.getStringExtra("key"+i);
                    Log.e("MPP",message.obj.toString());
                    new OffReceive(message.obj.toString());
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    }catch (Exception e)
                    {

                    }
                }
            }
        }).start();


        try
        {
            //对方ip地址  这个地址应该来自于服务器
            if(isOnline)
            {
                //如果在线，直接和这个ipUDP通信
                //ip_1 = InetAddress.getByName("192.168.43.133");
                ip_1 = InetAddress.getByName(ip);
                //获取本机IP地址
                ip_2 = InetAddress.getByName(getlocalip());
                //用自己的ip地址创建UDP Socket
                Socket_2 = new DatagramSocket(port, ip_2);
                Log.e("MP", "udp socket success");
                // 开启发送接收线程
                Receive r=new Receive(Socket_2,ip_2,port,adapter,msgList,msgListView);
                Thread receive=new Thread(r);
                receive.start();
                Send s=new Send(Socket_2,ip_1,port);
                final Thread send=new Thread(s);
                send.start();
                Send.onPause();
            }
            else
            {
                //如果是离线消息
                Toast.makeText(MainActivity.this,"Offline",Toast.LENGTH_SHORT).show();

            }
        }
        catch(Exception e)
        {
            Log.e("MP","udp socket error");
            e.printStackTrace();
        }
        //发送按钮
        msgsend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(isOnline) {
                    Send.onResume();
                    String content = inputText.getText().toString();
                    Message message = new Message();
                    message.what = SEND;
                    message.obj = content;
                    new SendBuf(content);
                    handler.sendMessage(message);

                }
                else
                {
                    // tcp 上传逻辑

                        new Thread(new Runnable(){
                            @Override
                            public void run()
                            {
                                try {
                                    socket = new Socket(ServerIP, 9006);
                                    String content = inputText.getText().toString();
                                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                                    out.println("3&"+LoginActivity.username + "&" + "Buser" + "&" + content+"&");
                                    out.flush();


                                    Log.e("MP", "tcp socket success");
                                    Message message = new Message();
                                    message.what = SEND;
                                    message.obj = content;
                                    new SendBuf(content);
                                    handler.sendMessage(message);
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

                }

        });


    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try
                {
                    //一个写死的服务器ip
                    Socket socket = new Socket(ServerIP, 9006);

                    //发送下线消息
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    out.println("4&"+LoginActivity.username+"&");
                    Log.e("MP", "tcpdown socket success"+LoginActivity.username);
                    out.close();
                    socket.close();
                }catch(Exception e)
                {
                    Log.e("MP", "tcpdown error");
                    e.printStackTrace();
                }
            }


        }).start();
    }

}
