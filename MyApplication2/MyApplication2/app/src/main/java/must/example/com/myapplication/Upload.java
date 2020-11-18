package must.example.com.myapplication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Upload {
    // 这里的地址为HOME
    private static final String HOME = "172.16.0.18";

    private static final String BUFF = "--";

    Socket socket = null;
    DataOutputStream output = null;
    DataInputStream input = null;

    public void uploadFile(final File file) {

        // 如果本系统为4.0以上（Build.VERSION_CODES.ICE_CREAM_SANDWICH为android4.0）
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // 详见StrictMode文档
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                    .penaltyLog().penaltyDeath().build());
        }

        new Thread(new Runnable(){
           @Override
            public void run()
           {
               try {
                   // 连接服务器
                   socket = new Socket(HOME, 9007);
                   // 得到输出流
                   output = new DataOutputStream(socket.getOutputStream());
                   // 得到如入流
                   input = new DataInputStream(socket.getInputStream());

                    /* 取得文件的FileInputStream */
                   FileInputStream fStream = new FileInputStream(file);

                   String[] fileEnd = file.getName().split("\\.");
                   output.writeUTF(BUFF + fileEnd[fileEnd.length - 1].toString());
                   System.out.println("buffer------------------" + BUFF
                           + fileEnd[fileEnd.length - 1].toString());

                   //设置每次写入102400bytes
                   int bufferSize = 102400;
                   byte[] buffer = new byte[bufferSize];
                   int length = 0;
                   // 从文件读取数据至缓冲区（值为-1说明已经读完）
                   while ((length = fStream.read(buffer)) != -1) {
                /* 将资料写入DataOutputStream中 */
                       output.write(buffer, 0, length);
                   }
                   // 一定要加上这句，否则收不到来自服务器端的消息返回
                   socket.shutdownOutput();
                   // 获取服务器验证数据

                   //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                   //String ms = br.readLine();
                   //Toast.makeText(ImageHandle,"upload",Toast.LENGTH_SHORT).show();

            /* close streams */
                   fStream.close();
                   output.flush();
                   socket.close();
            /* 取得input内容 */
                   String msg = input.readUTF();
                   System.out.println("上传成功  文件位置为：" + msg);

               } catch (UnknownHostException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               } catch (IOException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
           }

        }).start();


    }

}
