import java.io.BufferedOutputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.util.Date; 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter; 
  
/** 
 * @author �� 
 *  
 */  
class ImgSocketServer {  
	private static final int BUFSIZE=32; 
    private static final String BUFF = "--";  
  
    // ���ͼƬ�ļ���  
    private final static String IMG_RECORD = "D:\\img_record";  
  
    // socket�˿�  
    private final static int PORT = 9007;  
  
    /** 
     * @param args 
     */  
	 private static ServerSocket ss = null;  
        // socket������  
    private static Socket socket = null;  
    /*
    public static void main(String[] args) {  
        // ServerSocket������  
  
    }  
    */
    
    /** 
     * �ϴ� 
     *  
     * @param input 
     */  
    private static String SocketUpLoad(DataInputStream input) {  
        String fileName = null;  
        try {  
            DataInputStream inputStream = input;  
            // ��һ���ַ���  
            String msg = inputStream.readUTF();  
  
            String[] strings = msg.split(BUFF);  
            System.out.println(strings[strings.length - 1]);  
  
            // �ļ���  
            fileName = System.currentTimeMillis() + "."  
                    + strings[strings.length - 1];  
            System.out.println(new Date().toString() + "\t file name is :" + fileName);  
            // ����Ŀ¼  
            CreateDir(IMG_RECORD);  
  
            // �����ݶ�д���ļ���  
            BufferedOutputStream bo = new BufferedOutputStream(  
                    new FileOutputStream(new File(  
                            (IMG_RECORD + "\\" + fileName))));  
  
            int bytesRead = 0;  
            byte[] buffer = new byte[102400];  
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {  
                bo.write(buffer, 0, bytesRead);  
            }  
			 PrintWriter out1 = new PrintWriter(new BufferedWriter(
			   new OutputStreamWriter(socket.getOutputStream())),
			   true);
			 // ���÷�����Ϣ
			 out1.println("upload success");
			 out1.flush();
            // �ر�  
            bo.flush();  
            bo.close();  
            System.out.println(new Date().toString() + "\t receive data complete");  
  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return IMG_RECORD + "\\" + fileName;  
    }  
  
    /** 
     *  ����Ŀ¼���������򴴽��� 
     * @param dir 
     * @return 
     */  
    public static File CreateDir(String dir) {  
        File file = new File(dir);  
        if (!file.exists()) {  
            file.mkdirs();  
        }  
        return file;  
    }

	public static void ImgSocketServer() {
		// TODO Auto-generated method stub
		DataInputStream input = null;  
        DataOutputStream output = null;  
        byte[] receivBuf=new byte[BUFSIZE];  
        //insert into database
        String[] ReceiveData = null;
        String receivedData=null;
        String result = null;
		String receiveuser = null;
		String senduser = null;
		String content = null;
		String timestamp = null;
		
        try {  
            // ������8888�˿�  
            ss = new ServerSocket(PORT);  
            System.out.println("listen to " + PORT + "port");  
            System.out.println(new Date().toString() + " \n Server is open...");  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        while (true) {  
            try {  
                // �ȴ��ͻ������� ����ServerSocketʵ����accept����ȡ��һ���ͻ���Socket����  
                socket = ss.accept();  
                if (socket == null || socket.isClosed())  
                    continue;  
				
                input = new DataInputStream(socket.getInputStream());  
                // �õ������  
                output = new DataOutputStream(socket.getOutputStream());  
                
                String pathString = SocketUpLoad(input);  
  
                output.writeUTF(pathString);  

				receiveuser = "aaa";
				senduser = "bbb";
				content = "";
				System.out.println("pathString1:" + pathString);
				pathString = pathString.replaceAll("\\\\","\\\\\\\\");
				System.out.println("pathString2:" + pathString);
				result = ConnectDB.ChatConnection(receiveuser,senduser,content,pathString);
				
				
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } finally {  
                try {  
                    // �ر������  
                    if (output != null) {  
                        output.close();  
                    }  
                    // �ر�������  
                    if (input != null) {  
                        input.close();  
                    }  
                    // �ر�Socket����  
                    if (socket != null) {  
                        socket.close();  
                    }  
                } catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
            }  
        }  
	}  
  
}  