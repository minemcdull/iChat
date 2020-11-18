import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;

public class ChatServer {
	private static DatagramSocket Socket_2;
	private static InetAddress ip_1,ip_2;
	private static int port=8090;
	private static int servPort=9006;
	//Size of receive buffer  
    private static final int BUFSIZE=32; 
    private static String QuireData = null;
	private static void init()
	{
		/*
		try
		{
			ip_1=InetAddress.getByName("127.0.0.1");
			ip_2=InetAddress.getByName("127.0.0.2");
			
			Socket_2=new DatagramSocket(port,ip_2);
			
		
		}
		catch(Exception e)
		{
			System.out.println("Create Socket Error");
			e.printStackTrace();
		}
		*/
	}
		
	
	public static void main(String[] args)
	{
		//端对端
		init();
		//ForReceive r=new ForReceive(Socket_2,ip_2,port);
		//Thread receive=new Thread((Runnable)r);
		//ForSend s=new ForSend(Socket_2,ip_1,port);
		//Thread send=new Thread((Runnable)s);
		//send.start();
		//receive.start();
		//CSConnection();

		//端对服务器（文字）
		Thread ReceiveTextCon =new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				QuireData = CSConnection();
				System.out.println(QuireData);
			}
			
		});
		ReceiveTextCon.start();
		
		//端对服务器（图片）
		Thread ReceivePicCon =new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ImgSocketServer.ImgSocketServer();
				System.out.println(QuireData);
			}
			
		});
		ReceivePicCon.start();
		
	}
	
	//actionType 1-register 2-login 3-offlinechat 4-logout
	public static String CSConnection() {
		int recvMsgSize=0;
	    byte[] receivBuf=new byte[BUFSIZE];
		String receivedData=null;
		Socket clntSock=null;
		int actionType = 0;
		
		try {
			//1. 构造ServerSocket实例，指定服务端口。  
			ServerSocket servSock = new ServerSocket(servPort);  
			 System.out.println("已接收到客户端连接1");
			while(true)  
			{  
				// 2.调用accept方法，建立和客户端的连接  
			    clntSock = servSock.accept();  
			    SocketAddress clientAddress =      
			    clntSock.getRemoteSocketAddress();  
			    System.out.println("Handling client at " + clientAddress);  
			  
			    // 3. 获取连接的InputStream,OutputStream来进行数据读写  
			    InputStream in = clntSock.getInputStream();  
			    //OutputStream out = clntSock.getOutputStream();  
			           
			    //receive until client close connection,indicate by -l return 
			    while((recvMsgSize=in.read(receivBuf))!=-1){
			    	receivedData=new String(receivBuf);
			    	String[] ReceiveData = null;
			    	String password = null;
			    	String MACadd = null;
			    	String result = null;
					String receiveuser = null;
					String senduser = null;
					String content = null;
					String IP = null;
					String longtitude = null;
					String latitude = null;

					ReceiveData = receivedData.split("&");
					System.out.println("receivedData:"+receivedData);
					actionType = Integer.parseInt(ReceiveData[0]);
					System.out.println("actionType:"+actionType);
					
			    	switch(actionType){
			    		case 1:{
							//register查询数据库
							ReceiveData = receivedData.split("&");
							senduser = ReceiveData[1];
							password = ReceiveData[2];
							IP = ReceiveData[3];
							result = ConnectDB.RegisterConnection(senduser,password,IP);
							break;
			    		}
			    		
			    		case 2:{//2-login and revert
			    			ReceiveData = receivedData.split("&");
			    			senduser = ReceiveData[1];
							password = ReceiveData[2];
							IP = ReceiveData[3];
							System.out.println("receivedData:"+receivedData);
							System.out.println("senduser:"+senduser);
							System.out.println("password:"+password);
							result = ConnectDB.LoginConnection(senduser,password,IP);
							String[] tempresult = result.split("&");
							System.out.println("tempresult[0]:"+tempresult[0]);
							if (tempresult[0].equals("true")){
								String tempresult2 = ConnectDB.RevertConnection(senduser);
								result = result.trim() + tempresult2;
							}
							//result = "true&0"; //TODO
							break;
			    		}
			    		
			    		case 3:{//offlinechat
			    			ReceiveData = receivedData.split("&");
			    			senduser = ReceiveData[1];
			    			receiveuser = ReceiveData[2];							
							content = ReceiveData[3];
							System.out.println("receiveuser:"+receiveuser);
							System.out.println("senduser:"+senduser);
							System.out.println("content:"+content);
							result = ConnectDB.ChatConnection(receiveuser,senduser,content,"");
							break;	
			    		}
			    		
			    		case 4:{//4-logout
			    			ReceiveData = receivedData.split("&");
			    			senduser = ReceiveData[1];
							result = ConnectDB.LogoutConnection(senduser);
							break;
			    		}
			    		
			    		case 5:{//5-location
			    			ReceiveData = receivedData.split("&");
			    			senduser = ReceiveData[1];
			    			longtitude = ReceiveData[2];
			    			latitude = ReceiveData[3];
							result = ConnectDB.LocationConnection(senduser,longtitude,latitude);
							break;
			    		}
			    			
			    		default:{
			    			System.out.println("input error");
			    			break;
			    		}			    			
			    	}

			    	//result.getBytes();
					//System.out.println(temp);
			        //out.write(result.getBytes(),0,result.getBytes().length);
			        // 发送消息
			        PrintWriter out = new PrintWriter(new BufferedWriter(
			          new OutputStreamWriter(clntSock.getOutputStream())),
			          true);
			        // 设置返回信息
			        out.println(result);
			        out.flush();
			        System.out.println("result:"+result);
			        receivBuf=new byte[BUFSIZE];
			    }
			}
		    //clntSock.close();
		    //servSock.close();
		}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receivedData;
	}
	
}
