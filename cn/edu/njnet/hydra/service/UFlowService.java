package cn.edu.njnet.hydra.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.conf.NodeConfig;
import cn.edu.njnet.hydra.dbconn.DBconn;
import cn.edu.njnet.hydra.exenode.ovs.UFlow;
import hydra.log.HydraLogger;

public class UFlowService {

	private AtomicInteger count;
	
	@Resource
	private NodeConfig nodeConfig;
	
	private HydraLogger hydralogger = null;
	public UFlowService()
	{
		count = new AtomicInteger();
		hydralogger = new HydraLogger(NodeConfig.getNodeConfig().getNodeConfig("JOB_LOG"),"job_log");
	}

    public void clearStat(UFlow uf)
    {
		uf.setByte_count(0L);
		uf.setPacket_count(0L);
		uf.setDuraction_sec(0);
    }
    public void delete(UFlow uf)
    {
    	
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update u_flow_table set valid = %d,invalid_time=now() where id = %d;",
					   HydraConst.FLOW_INVALID,uf.getID());			
			String sql = fat.toString();
			stmt.executeUpdate(sql);
			fat.close();
			fat = new Formatter();
			fat.format("select JID,invalid_time,packet_count,byte_count from u_flow_table where id = %d;"
					, uf.getID());
			sql = fat.toString();
			ResultSet rs = stmt.executeQuery(sql);
			fat.close();
			fat = new Formatter();
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ctime = fmt.format(rs.getTimestamp("invalid_time"));
			fat.format("rule %s revoke: %s %spackets %sbytes", Integer.toString(rs.getInt("JID")),ctime,
					Long.toString(rs.getLong("packet_count")),Long.toString(rs.getLong("byte_count")));
			hydralogger.Write(fat.toString());
			rs.close();
			fat.close();
			stmt.close();
			count.decrementAndGet();
		} catch (SQLException e) {
			e.printStackTrace();
		}    	
    }
    public void addFlow(UFlow uf)
    {
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into u_flow_table("
					+ "ip_pattern,src_ip,dst_ip,start_time, u_action, JID, flow_stat,valid,submit_time) "
					+ "values(%d, %d, %d, %d, %d, %d, %d, %d, now());",
					+ uf.getIpPattern(),uf.getIpv4_src(),uf.getIpv4_dst(),uf.getStartt(),
					  uf.getUAct().value(), uf.getJID(),uf.getFlow_stat(), uf.getValid());			
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
                uf.setID(rs.getLong(1));
            count.incrementAndGet();
            rs.close();
            stmt.close();
            
            fat = new Formatter();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ctime = fmt.format(uf.getSubmitt());
            fat.format("rule %d submitted:%s %d %d %d %d %d %d", uf.getJID(),ctime,uf.getIpProto(),
            		uf.getIpv4_src(),uf.getSrc_port(),uf.getIpv4_dst(),uf.getDst_port(),uf.getUAct());
            hydralogger.Write(fat.toString());
            fat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}    	
    }
    public void deleteFlow(UFlow uf)
    {
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update u_flow_table set flow_stat = %d where ID = %d",
					+ HydraConst.FLOW_DEL,uf.getID());			
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}    	    	
    }
    public boolean checkFlowThreshold()
    {
    	int flowThreshold = Integer.valueOf(nodeConfig.getNodeConfig("MAX_FLOW"));
    	if(count.get() > flowThreshold)
    		return false;
    	else
    		return true;
    }
}
