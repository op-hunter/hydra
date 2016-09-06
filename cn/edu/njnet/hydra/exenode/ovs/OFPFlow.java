package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

import org.json.JSONObject;

import cn.edu.njnet.hydra.dbconn.DBconn;

public class OFPFlow {
	private Long     ID;
	private OFPMatch match;
	private OFPInst  inst;
	private OFPFlowStat stats;
	
	private int      table_id;
	private int      priority;
	private int      idle_timeout;
	private int      hard_timeout;
	private Long     cookie;
	private Long     cookie_mask;
	
    public OFPFlow()
    {
    	priority     = 0;
    	cookie       = 0L;
    	cookie_mask  = 0L;
    	idle_timeout = 0;
    	hard_timeout = 0;
    	table_id     = 0;
    	stats = new OFPFlowStat();
    }
    public OFPFlow(JSONObject js)
    {
    		
    }
    
    public JSONObject toJSONObject()
    {
    	JSONObject flow = new JSONObject();
    	flow.put("match", match.toJSONObject());
    	flow.put("actions", inst.toJSONObject());//ĿǰRYU����һ���ֻ��������⣬ֻ֧��APPLY_ACTION
    	flow.put("priority", priority);
    	flow.put("idle_timeout", idle_timeout);
    	flow.put("hard_timeout", hard_timeout);
    	flow.put("cookie", cookie);
    	flow.put("cookie_mask", cookie_mask);
    	flow.put("table_id", table_id);
    	return flow;
    }
    public void save()
    {
		
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into h_flow_table(table_id,priority,idle_timeout,hard_timeout,cookie,cookie_mask,submit_time) "
					+ " values(%d, %d, %d,%d, %d, %d,now());",
					   table_id,priority,idle_timeout,hard_timeout,cookie,cookie_mask);
			String sql = fat.toString();
			fat.close();
			int a = stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
                this.ID = (Long)rs.getObject(1);
            else
            	this.ID = null;
			System.out.println(sql);			
			rs.close();
			stmt.close();
			this.inst.setFlow(this);
			//this.inst.save();
			this.stats.setHflow(this);
			this.stats.save();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    public void delete()
    {
    	
    }
    public void update()
    {
    	stats.update();
    }
    //private 
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	public OFPMatch getMatch() {
		return match;
	}
	public void setMatch(OFPMatch match) {
		this.match = match;
	}
	public OFPInst getInst() {
		return inst;
	}
	public void setInst(OFPInst inst) {
		this.inst = inst;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Long getCookie() {
		return cookie;
	}
	public void setCookie(Long cookie) {
		this.cookie = cookie;
	}
	public Long getCookie_mask() {
		return cookie_mask;
	}
	public void setCookie_mask(Long cookie_mask) {
		this.cookie_mask = cookie_mask;
	}
	public int getIdle_timeout() {
		return idle_timeout;
	}
	public void setIdle_timeout(int idle_timeout) {
		this.idle_timeout = idle_timeout;
	}
	public int getHard_timeout() {
		return hard_timeout;
	}
	public void setHard_timeout(int hard_timeout) {
		this.hard_timeout = hard_timeout;
	}
	public OFPFlowStat getStats() {
		return stats;
	}
	public void setStats(OFPFlowStat stats) {
		this.stats = stats;
	}
    
}
