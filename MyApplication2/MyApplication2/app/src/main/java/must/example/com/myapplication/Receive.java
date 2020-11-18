package must.example.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/4/9.
 */

public class Receive extends Activity implements Runnable{

    private DatagramSocket socket;
    private InetAddress ip;
    private int port;
    //private TextView text;
    private MsgAdapter adapter;
    private ListView msgListView;

    private List<Msg> msgList=new ArrayList<Msg>();

    public Receive(DatagramSocket socket,InetAddress ip,int port,MsgAdapter adapter,List<Msg> msgList,ListView msgListView)
    {
        this.socket=socket;
        this.ip=ip;
        this.port=port;
        //this.text=text;
        this.adapter=adapter;
        this.msgList=msgList;
        this.msgListView=msgListView;
    }
    private int RECEIVE =0;
    //子线程返回数据更新UI
    private Handler handler =new Handler(){
        public void handleMessage(Message msg)
        {
            if(msg.what==RECEIVE)
            {
                Msg msgget=new Msg(msg.obj.toString(),Msg.TYPE_RECEIVED);
                msgList.add(msgget);
                // 当有新消息  刷新ListView中的显示
                adapter.notifyDataSetChanged();
                //将ListView定位到最后一行
                msgListView.setSelection(msgList.size());
            }
        }

    };
    public void run()
    {
        while(true)
        {
            byte[] buff=new byte[1024];
            DatagramPacket Receive=new DatagramPacket(buff,buff.length,ip,port);

            try
            {
                socket.receive(Receive);
                String message=new String(Receive.getData(),0,Receive.getLength());
                //System.out.println("\nFrom Client_1 :"+message);
                Log.e("MP",message);
                Message meg=new Message();
                meg.what=RECEIVE;
                meg.obj=message;
                handler.sendMessage(meg);
            }
            catch(Exception e)
            {
                //System.out.println("Receive Client_1 Error");
                e.printStackTrace();
                Log.e("MP","receive error");
            }
        }
    }

}