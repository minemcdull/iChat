import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ConnectDB {
	
	public static String RegisterConnection(String username, String password,String IP) {
		//String returnflag = null;
		Connection conn = null;
		int maxDeviceID = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ichatdemp?"
					+ "useSSL=false&characterEncoding=utf-8&user=root&password=");
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		String sql = "SELECT * FROM ichatdemp.login where UserName = '"
				+ username + "' and Password = '" + password + "'";
		
		System.out.println(sql);

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			if (rs.next()) {//已存在记录
				return "false";
			}else{
				String sqlmax = "SELECT max(DeviceID) FROM ichatdemp.login";
				System.out.println(sqlmax);
				try (Statement stmtmax = conn.createStatement(); ResultSet rsmax = stmtmax.executeQuery(sqlmax);) {
					while (rsmax.next()) {
						maxDeviceID = Integer.parseInt(rsmax.getString(1)) + 1; 
					}
				} catch (SQLException ex) {
					// handle any errors
					System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());
					final Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("SQLException: " + ex.getMessage());
					alert.showAndWait();
					return "false";
				}
				
				String sqlAdd1 = "insert into ichatdemp.login (DeviceID, UserName,Password, IP, accountstatue) value "
						+ "(" +  maxDeviceID + " , '" + username.trim() + "' , '" + password.trim() + "', '"+ IP + "', 1);";

				System.out.println("sqlAdd1:" + sqlAdd1);

				try (Statement stmtAdd = conn.createStatement();) {
					stmtAdd.executeUpdate(sqlAdd1);
					// Do something with the Connection
				} catch (SQLException ex) {
					// handle any errors
					System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());
					return "false";
				}
			}
			// Do something with the Connection
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return "false";
		}

		return "true";
	}
	
	public static String LoginConnection(String username, String password, String IP) {
		Connection conn = null;
		String returnIP = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ichatdemp?"
					+ "useSSL=false&characterEncoding=utf-8&user=root&password=");
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		String sql = "SELECT * FROM ichatdemp.login where UserName = '"
				+ username + "' and Password = '" + password + "'";
		
		System.out.println(sql);

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			if (rs.next()) {//记录存在
				if (rs.getString(4).equals("1")){//已登陆
					return "false";
				}else{
					String sqlUPD = "update ichatdemp.login set accountstatue = 1 , IP = '" + IP + "' where UserName = '" + 
							username + "'";
					System.out.println("sqlUPD:" + sqlUPD);

					try (Statement stmtAdd = conn.createStatement();) {
						stmtAdd.executeUpdate(sqlUPD);
						// Do something with the Connection
						//return "true";
					} catch (SQLException ex) {
						// handle any errors
						System.out.println("SQLException: " + ex.getMessage());
						System.out.println("SQLState: " + ex.getSQLState());
						System.out.println("VendorError: " + ex.getErrorCode());
						return "false";
					}
					//judge user
					int DeviceID = 0;
					if (username.equals("Auser")){
						DeviceID=1;
					}else{
						DeviceID=2;
					}
					
					String sqlIP = "SELECT IP FROM ichatdemp.login where DeviceID = "+ DeviceID;
					System.out.println(sqlIP);
					try (Statement stmtmax = conn.createStatement(); ResultSet rsIP = stmtmax.executeQuery(sqlIP);) {
						while (rsIP.next()) {
							returnIP = rsIP.getString(1); 
							return "true&"+returnIP; 
						}
					} catch (SQLException ex) {
						// handle any errors
						System.out.println("SQLException: " + ex.getMessage());
						System.out.println("SQLState: " + ex.getSQLState());
						System.out.println("VendorError: " + ex.getErrorCode());
						final Alert alert = new Alert(AlertType.INFORMATION);
						alert.setContentText("SQLException: " + ex.getMessage());
						alert.showAndWait();
						return "false";
					}
				}				
			}else{
				return "false";
			}
			
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			final Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("SQLException: " + ex.getMessage());
			alert.showAndWait();
			return "false";
		}
		return "false";
	}
	
	public static String ChatConnection(String receiveuser, String senduser, String content, String pathString) {
	
		//String returnflag = null;
		Connection conn = null;
		int maxrecordID = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ichatdemp?"
					+ "useSSL=false&characterEncoding=utf-8&user=root&password=");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String sqlmax = "SELECT max(recordID) FROM ichatdemp.chatrecord";
		System.out.println(sqlmax);
		try (Statement stmtmax = conn.createStatement(); ResultSet rsmax = stmtmax.executeQuery(sqlmax);) {
			while (rsmax.next()) {
				maxrecordID = Integer.parseInt(rsmax.getString(1)) +1 ; 
			}
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			final Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("SQLException: " + ex.getMessage());
			alert.showAndWait();
			return "false";
		}
		/* TODO
		String[] temppathString = pathString.split("\");
		System.out.println(temppathString[0]);
		System.out.println(temppathString[1]);
		System.out.println(temppathString[2]);
		String RtemppathString = null;
		for (int i=0;i<temppathString.length;i++){
			RtemppathString = RtemppathString + temppathString[i] + "\\";
		}
		System.out.println(RtemppathString);
		*/

		//0-未读 1-已读
		String sqlAdd1 = "insert into ichatdemp.chatrecord (recordID, recordstate,content, receiveuser, senduser, times , expiredate, PicturePath) value "
				+ "(" +  maxrecordID + " , 0 , '" + content + "','"+ receiveuser+ "','"
				+ senduser + "',now(),'9999-12-31','"+pathString+"' );";

		System.out.println("sqlAdd1:" + sqlAdd1);

		try (Statement stmtAdd = conn.createStatement();) {
			stmtAdd.executeUpdate(sqlAdd1);
			// Do something with the Connection
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return "false";
		}
		return "true";
		
	}
	
	public static String RevertConnection(String receiveuser) {
		Connection conn = null;
		String str = null; // 将StringBuffer转化成字符串
		StringBuffer sb = new StringBuffer(); // StringBuffer便于字符串的增删改查操作
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ichatdemp?"
					+ "useSSL=false&characterEncoding=utf-8&user=root&password=");
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		String sql = "SELECT * FROM ichatdemp.chatrecord where receiveuser = '" + receiveuser +"' and recordstate = 0";
		System.out.println(sql);
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			while (rs.next()) {
                sb.append("&"); // 在每条数据后面做标记，便于拆分
                sb.append(rs.getString(3));  
                //recordID, recordstate,content, receiveuser , senduser, timestamp , expiredate

			}
			
			str = sb.toString(); // 将数据由StringBuffer类型转化成String类型
			System.out.println("str:"+str);
			
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			final Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("SQLException: " + ex.getMessage());
			alert.showAndWait();
			return "false";
		}

		String sqlUPD = "update ichatdemp.chatrecord set recordstate = 1 where receiveuser = '" + receiveuser + "' and recordstate = 0";
		System.out.println("sqlUPD:" + sqlUPD);

		try (Statement stmtAdd = conn.createStatement();) {
			System.out.println(stmtAdd.executeUpdate(sqlUPD));
			// Do something with the Connection
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return "false";
		}
		
		return str;
	}
	
	
	public static String LogoutConnection(String username) {
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ichatdemp?"
					+ "useSSL=false&characterEncoding=utf-8&user=root&password=");
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		String sqlUPD = "update ichatdemp.login set accountstatue = 0 , IP = 0 where UserName = '" + 
				username + "' and accountstatue = 1 ;";
		System.out.println("sqlUPD:" + sqlUPD);

		try (Statement stmtAdd = conn.createStatement();) {
			if(stmtAdd.executeUpdate(sqlUPD)==1){
				return "true";
			}else{
				return "false";
			}
			// Do something with the Connection
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return "false";
		}
	}
	
	public static String LocationConnection(String username,String longtitude,String latitude) {
		Connection conn = null;
		int maxlocationID = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ichatdemp?"
					+ "useSSL=false&characterEncoding=utf-8&user=root&password=");
		} catch (SQLException e) {

			e.printStackTrace();
		}
		String sqlmax = "SELECT max(locationID) FROM ichatdemp.location";
		System.out.println(sqlmax);
		try (Statement stmtmax = conn.createStatement(); ResultSet rsmax = stmtmax.executeQuery(sqlmax);) {
			while (rsmax.next()) {
				maxlocationID = Integer.parseInt(rsmax.getString(1)) + 1; 
			}
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			final Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("SQLException: " + ex.getMessage());
			alert.showAndWait();
			return "false";
		}
		
		String sqlUPD = "insert into ichatdemp.location (locationID, locationstatue, username, longtitude, latitude) value ("+maxlocationID+",0,'" + 
				username + "','" + longtitude+ "','"+latitude+"');";
		System.out.println("sqlUPD:" + sqlUPD);

		try (Statement stmtAdd = conn.createStatement();) {
			if(stmtAdd.executeUpdate(sqlUPD)==1){
				return "true";
			}else{
				return "false";
			}
			// Do something with the Connection
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return "false";
		}
	}
	/*
	public static String PicConnection(String username) {
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ichatdemp?"
					+ "useSSL=false&characterEncoding=utf-8&user=root&password=");
		} catch (SQLException e) {

			e.printStackTrace();
		}
		DBConnection     DB = new DBConnection();    //负责连接MySQl数据库的类
		PreparedStatement     ps = null;
		InputStream    in = null; 
		try {
		//从本地硬盘硬盘读取一张图片保存到数据库
			con=DB.getConn();
		    in=new FileInputStream("sdf.png");
		    ps=con.prepareStatement("insert into test.phototest values(?,?)");
		    ps.setInt(1,2);
		    ps.setBinaryStream(2, in, in.available());
		    ps.executeUpdate();
		    in.close();
		    DB.closeConn(con);
		             
		  //从数据库读取图片保存到本地硬盘
		    con=DB.getConn();
		    ps=con.prepareStatement("select * from test.phototest where id=?");
		    ps.setInt(1,2);
		    rs=ps.executeQuery();
		    rs.next();    //将光标指向第一行
		    in=rs.getBinaryStream("photo");
		    byte[] b=new byte[in.available()];    //新建保存图片数据的byte数组
		    in.read(b);
		    OutputStream out=new FileOutputStream("222.jpg");
		    out.write(b);
		    out.flush();
		    out.close();
		    DB.closeConn(con);
		} 
		catch (Exception e) {
			System.out.println("Error::"+e);
		}
		
	}
	*/
}
