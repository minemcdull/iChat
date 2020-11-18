package must.example.com.myapplication;

/**
 * Created by Lenovo on 2017/4/9.
 */

import java.net.*;


public class Send implements Runnable {
    private DatagramSocket socket;
    private InetAddress ip;
    private int port;
    private static boolean mPaused;
    private boolean mFinished;
    private static Object mPauseLock;

    public Send(DatagramSocket socket,InetAddress ip,int port)
    {
        this.socket=socket;
        this.ip=ip;
        this.port=port;
        mPauseLock = new Object();
        mPaused = false;
        mFinished = false;
    }
    public static void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }


    public static void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }
    public void run()
    {
        while(!mFinished)
        {
            try
            {
            Thread.sleep(200);
            String str=SendBuf.Get();
            byte[] buff=str.getBytes();

            DatagramPacket Send=new DatagramPacket(buff,buff.length,ip,port);
                if(buff.length>0)
                    socket.send(Send);
                onPause();
            }
            catch(Exception e)
            {
                //System.out.println("Client_1 Send Error");
                e.printStackTrace();
            }
            synchronized (mPauseLock)
            {
                while (mPaused)
                {
                    try
                    {
                        mPauseLock.wait();
                    } catch (InterruptedException e)
                    {

                    }
                }
            }

        }
    }
}