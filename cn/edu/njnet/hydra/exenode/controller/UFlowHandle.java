package cn.edu.njnet.hydra.exenode.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.conf.NodeConfig;
import cn.edu.njnet.hydra.dbconn.DBconn;
import cn.edu.njnet.hydra.exenode.ovs.UAction;
import cn.edu.njnet.hydra.exenode.ovs.UFlow;
import cn.edu.njnet.hydra.service.UFlowService;

public class UFlowHandle {

	@Resource
	private NodeConfig nodeConfig;
	@Resource
	private UFlowService uFlowService;
	
	private List<UFlow> addflow;	
	private List<UFlow> deleteflow;	
	private static HashMap<Long,UFlow> allflow;

	private int statCycle;
	private long lastUpdate;
	public UFlowHandle()
	{
		allflow = new HashMap<Long,UFlow>();
	}
	public void init()
	{
		statCycle = Integer.valueOf(nodeConfig.getNodeConfig("STATUS_CYCLE")) * 1000;
		lastUpdate = 0;
	}
    public List<UFlow> getToAddUserflow()
    {
    	try
    	{
    		Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("select * from u_flow_table where valid=1 and flow_stat=%d and start_time < %d ;",
					HydraConst.FLOW_ADD,System.currentTimeMillis()/1000);
			String sql = fat.toString();
			//System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<UFlow> addflow = new ArrayList<UFlow>();
			while(rs.next())
			{
				UFlow uf = new UFlow();
				uf.setID(rs.getLong("ID"));
				uf.setIpv4_src(rs.getLong("src_ip"));
				uf.setSrc_mask(rs.getLong("src_mask"));
				uf.setIpv4_dst(rs.getLong("dst_ip"));
				uf.setDst_mask(rs.getLong("dst_mask"));
				uf.setIpPattern(rs.getInt("ip_pattern"));
				uf.setIpProto(rs.getInt("ip_proto"));
				uf.setSrc_port(rs.getInt("src_port"));
				uf.setDst_port(rs.getInt("dst_port"));
				uf.setPort_pattern(rs.getInt("port_pattern"));
				uf.setMaxbyte(rs.getLong("max_bytes"));
				uf.setMaxpacket(rs.getLong("max_packets"));
				uf.setHard_time(rs.getLong("hard_time"));
				uf.setFlow_stat(rs.getInt("flow_stat"));
				uf.setValid(rs.getInt("valid"));
				uf.setStartt(rs.getLong("start_time"));
				uf.setEndt(rs.getLong("end_time"));
				uf.setSubmitt(rs.getTimestamp("submit_time"));
				uf.setInvalidt(rs.getTimestamp("invalid_time"));
				uf.setJID(rs.getInt("JID"));
				uf.setUAct(UAction.valueOf(rs.getInt("u_action")));
				addflow.add(uf);
			}
			this.addflow = addflow;
			stmt.close();
			rs.close();
			return addflow;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
    public List<UFlow> getToDeleteflow()
    {
    	try
    	{
    		Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			String sql = "select * from u_flow_table where valid=1 and flow_stat=" + HydraConst.FLOW_DEL;
			//System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<UFlow> delflow = new ArrayList<UFlow>();
			while(rs.next())
			{
				UFlow uf = new UFlow();
				uf.setID(rs.getLong("ID"));
				uf.setIpv4_src(rs.getLong("src_ip"));
				uf.setSrc_mask(rs.getLong("src_mask"));
				uf.setIpv4_dst(rs.getLong("dst_ip"));
				uf.setDst_mask(rs.getLong("dst_mask"));
				uf.setIpPattern(rs.getInt("ip_pattern"));
				uf.setIpProto(rs.getInt("ip_proto"));
				uf.setSrc_port(rs.getInt("src_port"));
				uf.setDst_port(rs.getInt("dst_port"));
				uf.setPort_pattern(rs.getInt("port_pattern"));
				uf.setMaxbyte(rs.getLong("max_bytes"));
				uf.setMaxpacket(rs.getLong("max_packets"));
				uf.setFlow_stat(rs.getInt("flow_stat"));
				uf.setValid(rs.getInt("valid"));
				uf.setStartt(rs.getLong("start_time"));
				uf.setEndt(rs.getLong("end_time"));
				uf.setSubmitt(rs.getTimestamp("submit_time"));
				uf.setInvalidt(rs.getTimestamp("invalid_time"));
				uf.setJID(rs.getInt("JID"));
				uf.setUAct(UAction.valueOf(rs.getInt("u_action")));
				delflow.add(uf);
			}
			this.deleteflow = delflow;
			return delflow;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		this.deleteflow = null;
    		return null;
    	}
    }
    public void updateAddUserflow()
    {
    	for(UFlow uflow : addflow)
    	{
    		uflow.updateAdd();
    		allflow.put(uflow.getID(),uflow);
    	} 
    }
    public void deleteUserflow()
    {
    	for(UFlow uflow : deleteflow)
    	{
    		uflow.delete();
    		allflow.remove(uflow.getID());
    	}    	
    }
    public void updateState(JSONArray json)
    {
    	for(int i = 0;i < json.length();i++)
    	{
    		JSONObject jo = (JSONObject) json.get(i);
    		Long cookie = jo.getLong("cookie");
    		long id = cookie & 0x00000000ffffffffL;
    		UFlow uf = allflow.get(id);
    		if(uf != null)
    		{
    		    //uf.setByte_count(uf.getByte_count()+jo.getLong("byte_count"));
    		    uf.setPacket_count(uf.getPacket_count()+jo.getLong("packet_count"));
    		    uf.setDuraction_sec(jo.getInt("duration_sec"));
    		}
    	}
    }
    public void updateLogStat()
    {
    	long curr = System.currentTimeMillis();
    	if(curr - lastUpdate > statCycle)
    	{
    		_updateLogStat();
    		lastUpdate = curr;
    	}
    }
    private void _updateLogStat() {
    	Iterator<Entry<Long, UFlow> > iter = allflow.entrySet().iterator();
    	while(iter.hasNext())
    	{
    		UFlow uf = iter.next().getValue();
    		uf.updateLogStat();
    	}    
		
	}
	public void updateStatToDb()
    {
    	Iterator<Entry<Long, UFlow> > iter = allflow.entrySet().iterator();
    	while(iter.hasNext())
    	{
    		UFlow uf = iter.next().getValue();
    		uf.update();
    	}    	
    }
    public void clearState()
    {
    	Iterator<Entry<Long, UFlow> > iter = allflow.entrySet().iterator();
    	while(iter.hasNext())
    	{
    		UFlow uf = iter.next().getValue();
    		uFlowService.clearStat(uf);
    	}
    }
}
