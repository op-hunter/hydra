package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;
import org.json.JSONObject;

import cn.edu.njnet.hydra.dbconn.DBconn;

public class HController {
	private long id;
    private String restUrl;
    private String ipString;
    private int port;
    private String name;
    private int cpuUsage;
    private int memUsage;
    private int LoadAvg;
    
    private int stat;
    
    public HController()
    {
    	
    }
	public HController(JSONObject jo) {
    	
    	restUrl = jo.getString("restURL");
		ipString = jo.getString("ip");
		port     = jo.getInt("port");
		name     = jo.getString("name");
	}
	public void updateStat(JSONObject jo)
	{
		cpuUsage = (int)(jo.getDouble("cpu") * 100);
		memUsage = (int)(jo.getDouble("mem") * 100);
		LoadAvg  = (int)(jo.getDouble("load_avg") * 100);
	}
	public void save() {
  		try {
 			Connection conn = DBconn.createConnection();
 			Statement stmt = conn.createStatement();
 			Formatter fat = new Formatter();
 			fat.format("insert into h_controller(name,ip,port,cpu,mem,load_avg,stat,update_time) "
 					+ " values('%s',inet_aton('%s'),%d,%d,%d, %d, %d,now());",
 					name,ipString, port, cpuUsage, memUsage, LoadAvg, stat);
 			String sql = fat.toString();
 			fat.close();
			fat.close();
			stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
                this.id = (Long)rs.getObject(1);
            else
            	this.id = 0;		
 			rs.close();
 			stmt.close();
 			
 		} catch (SQLException e) {
 			e.printStackTrace();
 		} 
	}
	public void update()
	{
  		try {
 			Connection conn = DBconn.createConnection();
 			Statement stmt = conn.createStatement();
 			Formatter fat = new Formatter();
 			fat.format("select * from h_controller where ip = inet_aton('%s') and port=%d;",ipString,port);
 			String sql = fat.toString();
 			fat.close();
 			ResultSet rs = stmt.executeQuery(sql);
 			if(rs.next())
 			{
 				id = rs.getLong("id");
 				_update();
 			}
 			else
 			{
 				save();
 			}
 			rs.close();
 			stmt.close();
 			
 		} catch (SQLException e) {
 			e.printStackTrace();
 		}
	}
    private void _update() {
  		try {
 			Connection conn = DBconn.createConnection();
 			Statement stmt = conn.createStatement();
 			Formatter fat = new Formatter();
 			fat.format("update h_controller "
 					+"set name='%s',ip=inet_aton('%s'),port=%d,cpu=%d,mem=%d,load_avg=%d,stat=%d,update_time=now() "
 					+"where id=%d",name,ipString, port, cpuUsage, memUsage, LoadAvg, stat,id);
 			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);		
 			stmt.close();			
 		} catch (SQLException e) {
 			e.printStackTrace();
 		} 			
	}
	public String getRestUrl() {
		return restUrl;
	}
	public void setRestUrl(String restUrl) {
		this.restUrl = restUrl;
	}
	public String getIpString() {
		return ipString;
	}
	public void setIpString(String ipString) {
		this.ipString = ipString;
	}
	public int getStat() {
		return stat;
	}
	public void setStat(int stat) {
		this.stat = stat;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void clearStat() {
		cpuUsage = 0;
		memUsage = 0;
		LoadAvg  = 0;	
	}
	
}
