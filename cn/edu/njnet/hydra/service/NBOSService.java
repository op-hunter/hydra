package cn.edu.njnet.hydra.service;

import hydra.ddos.pojo.AttackInfo;
import hydra.ddos.pojo.OppositeInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import cn.edu.njnet.hydra.conf.NodeConfig;
import cn.edu.njnet.hydra.dbconn.DBconn;
import cn.edu.njnet.hydra.exenode.ovs.UFlow;

public class NBOSService {
	
	private AtomicInteger count;
	
	@Resource
	private NodeConfig nodeConfig;
	
	public NBOSService()
	{
		count = new AtomicInteger();
	}
	public void saveOppositeInfo(OppositeInfo info)
	{
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into nbos_opposite_info(opposite_ip, "
					+ "location, pkts_out, pkts_in, bytes_out, "
					+ "bytes_in, port, ddos_id, gran) values(%d, "
					+ "'%s', %d, %d, %d,"
					+ "%d, %d, %d, %d)",info.getOpposite_ip(),
					  info.getOpposite_location(), info.getPkts_out(), 
					  info.getPkts_in(), info.getBytes_out(),
					  info.getPkts_in(), info.getPort(), info.getDdos_id(), info.getCurrentGran());
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);
			count.incrementAndGet();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void delete(UFlow uf)
	{
		uf.delete();
		count.decrementAndGet();
	}
	public void saveAttackInfo(AttackInfo info)
	{
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into nbos_attack_info(ddos_id, "
					+ "ip, location, ddos_type, avg_pps, "
					+ "max_pps, avg_kbps, max_kbps, start_time,"
					+ "end_time, granularity_num, gran) values(%d, "
					+ "%d, '%s', %d, %d,"
					+ "%d, %d, %d, %d,"
					+ "%d, %d, %d)",info.getDdos_id(),
					info.getIp(), info.getLocation(), info.getDdos_type(), info.getAvg_pps(),
					info.getMax_pps(), info.getAvg_kbps(), info.getMax_kbps(), info.getStart_time(),
					info.getEnd_time(), info.getGranularity_num(),info.getCurrentGran());
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	public boolean checkDDOSFlowThreshold()
	{
    	int flowThreshold = Integer.valueOf(nodeConfig.getNodeConfig("DDOS_MAX_FLOW"));
    	if(count.get() > flowThreshold)
    		return false;
    	else
    		return true;		
	}
	public boolean checkPPSThreshold(AttackInfo attack) {
		String threshold = nodeConfig.getNodeConfig("DDOS_OUT_THRESHOLD");
		Integer thresholdInt = Integer.valueOf(threshold);
		if(attack.getAvg_pps() > thresholdInt)
		    return true;
		else
			return false;
	}
}
