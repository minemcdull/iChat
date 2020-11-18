import java.net.*;
import java.util.Scanner;

public class ForSend implements Runnable {
	private DatagramSocket socket;
	private InetAddress ip;
	private int port;
	private Scanner sc=new Scanner(System.in);
	public ForSend(DatagramSocket socket,InetAddress ip,int port)
	{
		this.socket=socket;
		this.ip=ip;
		this.port=port;
	}
	public void run()
	{
		while(true)
		{
			//System.out.print("Client_2 :");
			String str=sc.nextLine();
			byte[] buff=str.getBytes();
			DatagramPacket Send=new DatagramPacket(buff,buff.length,ip,port);
			try
			{
				socket.send(Send);
			}
			catch(Exception e)
			{
				System.out.println("Send Client_2 Error");
				e.printStackTrace();
			}
		}
	}
	

}
