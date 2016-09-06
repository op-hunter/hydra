package cn.edu.njnet.hydra.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

import cn.edu.njnet.hydra.conf.ConstConf;
import cn.edu.njnet.hydra.dbconn.DBconn;

public class ExeNodeService {
    
	private long lastUpdateTime;
	public ExeNodeService()
	{
		lastUpdateTime = System.currentTimeMillis()/1000;
	}
	public void updateExeNodeStat() {
		Runtime run = Runtime.getRuntime(); 
		long max = run.maxMemory(); 
		long total = run.totalMemory(); //JVM 目前申请的内存
		long free = run.freeMemory();//可用内存等于MAX - total + FREE
		long currStamp = System.currentTimeMillis()/1000;		
		long cycleTime = currStamp - lastUpdateTime;
		updateExeNodeAttr(ConstConf.FREE_MEMORY,String.valueOf(free));
		updateExeNodeAttr(ConstConf.MAX_MEMORY,String.valueOf(max));
		updateExeNodeAttr(ConstConf.TOTAL_MEMORY,String.valueOf(total));
		updateExeNodeAttr(ConstConf.UPDATE_TIME,String.valueOf(currStamp));		
		updateExeNodeAttr(ConstConf.CYCLE_TIME,String.valueOf(cycleTime));	
		lastUpdateTime = currStamp;
	}
	public void updateExeNodeAttr(String key,String value)
	{
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update node_stat set value='%s' where name='%s'",value,key);
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

}
