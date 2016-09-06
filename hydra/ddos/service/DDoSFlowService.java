package hydra.ddos.service;

import hydra.ddos.pojo.AttackFlow;
import hydra.ddos.pojo.DirectDDoSEntry;
import hydra.ddos.pojo.IndirectDDoSEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.dbconn.DBconn;
import cn.edu.njnet.hydra.exenode.ovs.UAction;
import cn.edu.njnet.hydra.exenode.ovs.UFlow;
import hydra.log.HydraLogger;
import cn.edu.njnet.hydra.conf.NodeConfig;

public class DDoSFlowService {
	private HashMap<AttackFlow, Integer> currResponseFlow = new HashMap<AttackFlow, Integer>();
	
	private HydraLogger hydralogger = null;
	
	public DDoSFlowService(){
		hydralogger = new HydraLogger(NodeConfig.getNodeConfig().getNodeConfig("JOB_LOG"),"job_log");
	}
	
	public void addResponseUFlowList(ArrayList<UFlow> uflowList) {
		Connection conn = DBconn.getDDoSConnection();
		String sql = "insert into u_flow_table("
				+ "ip_pattern,src_ip,dst_ip,start_time, u_action, JID, flow_stat,valid,submit_time) "
				+ "values(?, ?, ?, ?, ?, ?, ?, ?, now());";
		try {
			/*
			 * 经过目前的分析，怀疑这里有内存泄露，具体内容还需要进一步的分析
			 */
			PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			for (UFlow uf : uflowList) {
				stmt.setInt(1, uf.getIpPattern());
				stmt.setLong(2, uf.getIpv4_src());
				stmt.setLong(3, uf.getIpv4_dst());
				stmt.setLong(4, uf.getStartt());
				stmt.setInt(5, uf.getUAct().value());
				stmt.setInt(6, uf.getJID());
				stmt.setInt(7, uf.getFlow_stat());
				stmt.setInt(8, uf.getValid());
				stmt.executeUpdate();
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next())
					uf.setID(rs.getLong(1));
				rs.close();
			}
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据直接攻击项和攻击流列表，生成阻断流表项
	 * @param currentDirectEntry
	 * @param attackFlowList
	 */
	public void commitL1AttackResponse(DirectDDoSEntry currentDirectEntry) {
		List<AttackFlow> attackFlowList = currentDirectEntry.getAttackFlowList();
		ArrayList<UFlow> uflowList = new ArrayList<UFlow>();
		for(AttackFlow attackFlow : attackFlowList)
		{
		    UFlow flow = new UFlow();
		    flow.setIpv4_src(attackFlow.getSrcIP());
		    flow.setIpv4_dst(attackFlow.getDstIP());
		    flow.setJID((int) currentDirectEntry.getResponseJob().getID());
		    flow.setValid(HydraConst.FLOW_VALID);
		    flow.setFlow_stat(HydraConst.FLOW_ADD);
		    flow.setIpPattern(1);
		    flow.setUAct(UAction.DROP);
		    flow.setStartt(System.currentTimeMillis()/1000);
		    uflowList.add(flow);
		}
		addResponseUFlowList(uflowList);
		return ;
	}
	public void cancelL1AttackResponse(DirectDDoSEntry directDDoSEntry) {
		try 
		{
			Connection conn = DBconn.getDDoSConnection();
			Statement stmt = conn.createStatement();
			long jid = directDDoSEntry.getResponseJob().getID();
			Formatter fat = new Formatter();
			fat.format("update u_job_table set type = %d where ID = %d;",
					HydraConst.TABLE_STOP, jid);			
			String sql = fat.toString();
			stmt.executeUpdate(sql);
			fat.close();
			fat = new Formatter();
			fat.format("update u_flow_table set flow_stat = %d where JID = %d;",
					HydraConst.FLOW_DEL,jid);			
			sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		
	}
	public void commitL2AttackResponse(IndirectDDoSEntry currentIndirectEntry) {
		List<AttackFlow> attackFlowList = currentIndirectEntry.getAttackFlowList();
		ArrayList<UFlow> uflowList = new ArrayList<UFlow>();
		for(AttackFlow attackFlow : attackFlowList)
		{
		    UFlow flow = new UFlow();
		    flow.setIpv4_src(attackFlow.getSrcIP());
		    flow.setIpv4_dst(attackFlow.getDstIP());
		    flow.setJID((int) currentIndirectEntry.getResponseJob().getID());
		    flow.setValid(HydraConst.FLOW_VALID);
		    flow.setFlow_stat(HydraConst.FLOW_ADD);
		    flow.setIpPattern(1);
		    flow.setUAct(UAction.DROP);
		    flow.setStartt(System.currentTimeMillis()/1000);
		    uflowList.add(flow);
		}
		addResponseUFlowList(uflowList);
		return ;
		
	}
	public void cancelL2AttackResponse(IndirectDDoSEntry indirectDDoSEntry) {
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			long jid = indirectDDoSEntry.getResponseJob().getID();
			Formatter fat = new Formatter();
			fat.format("update u_job_table set type = %d where ID = %d;",
					HydraConst.TABLE_INVALID, jid);			
			String sql = fat.toString();
			stmt.executeUpdate(sql);			
			fat.close();
			fat = new Formatter();
			fat.format("update u_flow_table set flow_stat = %d where JID = %d;",
					HydraConst.FLOW_DEL, jid);			
			sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}   
		
	}


}
