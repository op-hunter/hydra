package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.conf.NodeConfig;
import cn.edu.njnet.hydra.dbconn.DBconn;
import hydra.log.HydraLogger;

public class UFlow {
    private long ID;
    private Long ipv4_src;
    private Long ipv4_dst;
    private Long src_mask;
    private Long dst_mask;
    private Integer ipProto;
    private Integer ipPattern;
    private Integer src_port;
    private Integer dst_port;
    private Integer port_pattern;
    private Long maxpacket;
    private Long maxbyte;
    private Long hard_time;
    private Integer valid;
    private Integer flow_stat;
    private Integer JID;
    private Long startt;
    private Long endt;
    private Date submitt;
    private Date invalidt;
    private UAction UAct;
    private Integer duraction_sec;
    private Long packet_count;
    private Long byte_count;
    private UFlowStat stat;
    
    private HydraLogger hydralogger = null;
    public UFlow()
    {
    	hydralogger = new HydraLogger(NodeConfig.getNodeConfig().getNodeConfig("JOB_LOG"),"job_log");
    }
    public void delete()
    {
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update u_flow_table set valid = %d,invalid_time=now() where id = %d;",
					   HydraConst.FLOW_INVALID,ID);			
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);	
			fat = new Formatter();
			fat.format("select JID,invalid_time,packet_count,byte_count from u_flow_table where id = %d;"
					, ID);
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
		} catch (SQLException e) {
			e.printStackTrace();
		}    	
    }
    public void updateAdd()
    {
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update u_flow_table set flow_stat = %d where id = %d;",HydraConst.FLOW_STD,ID);
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);			
		} catch (SQLException e) {
			e.printStackTrace();
		}       	
    }
    public void update()
    {
    	try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update u_flow_table set duraction_sec=%d,packet_count=%d,byte_count=%d,update_time=now()"
					+ " where id=%d",
					   duraction_sec,packet_count,byte_count,ID);
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	checkValid();
    }
	private void checkValid() 
	{
		boolean valid = true;
		if(hard_time != 0 && duraction_sec > hard_time)
		{
			valid = false;
		}
		if(maxbyte != 0 && byte_count > maxbyte )
		{
			valid = false;
		}
		if(maxpacket!= 0 && packet_count > maxpacket )
		{
			valid = false;
		}
		if(valid == false)
		{
			setInValid();
		}
		
	}
	private void setInValid() {
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update u_flow_table set flow_stat=%d where id =%d;",
					 HydraConst.FLOW_DEL, ID);
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
    public void updateLogStat()
    {
    	stat = new UFlowStat();
    	stat.setByte_count(byte_count);
    	stat.setPacket_count(packet_count);
    	stat.setDuraction_sec(duraction_sec);
    	stat.setUf(this);
    	stat.save();
    }
	public long getID() {  
		return ID;
	}
	public void setID(long iD) {
		ID = iD;
	}
	public Long getIpv4_src() {
		return ipv4_src;
	}
	public void setIpv4_src(Long ipv4_src) {
		this.ipv4_src = ipv4_src;
	}
	public Long getIpv4_dst() {
		return ipv4_dst;
	}
	public void setIpv4_dst(Long ipv4_dst) {
		this.ipv4_dst = ipv4_dst;
	}
	public Long getSrc_mask() {
		return src_mask;
	}
	public void setSrc_mask(Long src_mask) {
		this.src_mask = src_mask;
	}
	public Long getDst_mask() {
		return dst_mask;
	}
	public void setDst_mask(Long dst_mask) {
		this.dst_mask = dst_mask;
	}
	public Integer getIpProto() {
		return ipProto;
	}
	public void setIpProto(Integer ipProto) {
		this.ipProto = ipProto;
	}
	public Integer getIpPattern() {
		return ipPattern;
	}
	public void setIpPattern(Integer ipPattern) {
		this.ipPattern = ipPattern;
	}
	public Integer getSrc_port() {
		return src_port;
	}
	public void setSrc_port(Integer src_port) {
		this.src_port = src_port;
	}
	public Integer getDst_port() {
		return dst_port;
	}
	public void setDst_port(Integer dst_port) {
		this.dst_port = dst_port;
	}
	public Integer getPort_pattern() {
		return port_pattern;
	}
	public void setPort_pattern(Integer port_pattern) {
		this.port_pattern = port_pattern;
	}
	public Long getMaxpacket() {
		return maxpacket;
	}
	public void setMaxpacket(Long maxpacket) {
		this.maxpacket = maxpacket;
	}
	public Long getMaxbyte() {
		return maxbyte;
	}
	public void setMaxbyte(Long maxbyte) {
		this.maxbyte = maxbyte;
	}
	public Long getHard_time() {
		return hard_time;
	}
	public void setHard_time(Long hard_time) {
		this.hard_time = hard_time;
	}
	public int getValid() {
		return valid;
	}
	public void setValid(int valid) {
		this.valid = valid;
	}
	public int getFlow_stat() {
		return flow_stat;
	}
	public void setFlow_stat(int flow_stat) {
		this.flow_stat = flow_stat;
	}
	public Integer getJID() {
		return JID;
	}
	public void setJID(Integer jID) {
		JID = jID;
	}
	public Long getStartt() {
		return startt;
	}
	public void setStartt(Long startt) {
		this.startt = startt;
	}
	public Long getEndt() {
		return endt;
	}
	public void setEndt(Long endt) {
		this.endt = endt;
	}
	public Date getSubmitt() {
		return submitt;
	}
	public void setSubmitt(Date submitt) {
		this.submitt = submitt;
	}
	public Date getInvalidt() {
		return invalidt;
	}
	public void setInvalidt(Date invalidt) {
		this.invalidt = invalidt;
	}
	public UFlowStat getStat() {
		return stat;
	}
	public void setStat(UFlowStat stats) {
		this.stat = stats;
	}
	public UAction getUAct() {
		return UAct;
	}
	public void setUAct(UAction uAct) {
		UAct = uAct;
	}
	public Integer getDuraction_sec() {
		return duraction_sec;
	}
	public void setDuraction_sec(Integer duraction_sec) {
		this.duraction_sec = duraction_sec;
	}
	public Long getPacket_count() {
		return packet_count;
	}
	public void setPacket_count(Long packet_count) {
		this.packet_count = packet_count;
	}
	public Long getByte_count() {
		return byte_count;
	}
	public void setByte_count(Long byte_count) {
		this.byte_count = byte_count;
	}
	
}
