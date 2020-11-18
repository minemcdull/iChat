import java.net.*;

public class ForReceive implements Runnable{
	
	private DatagramSocket socket;
	private InetAddress ip;
	private int port;
	
	public ForReceive(DatagramSocket socket,InetAddress ip,int port)
	{
		this.socket=socket;
		this.ip=ip;
		this.port=port;
	}
	
	public void run()
	{
		while(true)
		{
			byte[] buff=new byte[1024];
			DatagramPacket Receive=new DatagramPacket(buff,buff.length,ip,port);
			System.out.println(Receive);
			
			try
			{
				socket.receive(Receive);
				String message=new String(Receive.getData(),0,Receive.getLength());
				System.out.println("\nFrom Client_1 :"+message);
			}
			catch(Exception e)
			{
				System.out.println("Receive Client_1 Error");
				e.printStackTrace();
			}
		}
	}

}
