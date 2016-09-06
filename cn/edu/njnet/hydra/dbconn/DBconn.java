package cn.edu.njnet.hydra.dbconn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import cn.edu.njnet.hydra.conf.NodeConfig;
import static java.lang.System.out;
public class DBconn {
    private static String connurl;
    private static Connection conn = null;//主线程数据库连接
    private static Connection logConn = null;//接收线程
    private static Connection ddosConn = null;//DDos线程的数据库连接
    private static final Object lock = new Object();
    private DBconn()
    {
    	
    }
    public void init()
    {
    	
    }
    /**
     * 
     * @return
     */
    public static Connection createConnection()
    {
    	if(conn == null)
    	{
			synchronized (lock) {
				try {
					Class.forName("com.mysql.jdbc.Driver");
					if (connurl == null)
						connurl = NodeConfig.getNodeConfig().getNodeConfig(
								"CONNURL");
					conn = DriverManager.getConnection(connurl);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Database connected error");
					System.exit(1);
				}
				return conn;
			}
    	}
    	else
    	{
    		return conn;
    	}
    }
    /**
     * 
     * @return
     */
    public static Connection getDDoSConnection()
    {
    	if(ddosConn == null)
    	{
			synchronized (lock) {
				try {
					Class.forName("com.mysql.jdbc.Driver");
					if (connurl == null)
						connurl = NodeConfig.getNodeConfig().getNodeConfig(
								"CONNURL");
					ddosConn = DriverManager.getConnection(connurl);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Database connected error");
					System.exit(1);
				}
				return ddosConn;
			}
    	}
    	else
    	{
    		return ddosConn;
    	}
    }
    /**
     * 
     * @return
     */
    public static Connection getLogConnection()
    {
    	if(logConn == null)
    	{
			synchronized (lock) {
				try {
					Class.forName("com.mysql.jdbc.Driver");
					if (connurl == null)
						connurl = NodeConfig.getNodeConfig().getNodeConfig(
								"CONNURL");
					logConn = DriverManager.getConnection(connurl);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Database connected error");
					System.exit(1);
				}
				return logConn;
			}
    	}
    	else
    	{
    		return logConn;
    	}
    }
}
