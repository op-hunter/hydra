package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;
import java.util.List;

import org.json.JSONObject;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.dbconn.DBconn;

public class OFPSwitch {
     private long id;
     private Long n_buffer;
     private Long n_tables;
     private Long auxiliary_id;
     private Long capabilities;
     private long dpid;
     private Integer stat;
     private int flows;
     private String virtualip;
     
     private long generation_id;
     private List<OFPPort> pl;
     private HController slaveController;
     private HController masterController;
     
     public OFPSwitch()
     {
    	 id = 0;
    	 dpid = 0;
    	 stat = HydraConst.OVS_STD;
         n_buffer     = 256L;
         n_tables     = 256L;
         auxiliary_id = 0L;
         capabilities = 71L;
     }
     public OFPSwitch(JSONObject js)
     {
    	 id = 0;
    	 dpid = js.getLong("dpid");
    	 getDefaultSwitchInfo(js);
         n_buffer     = js.getLong("n_buffer");
         n_tables     = js.getLong("n_tables");
         auxiliary_id = js.getLong("auxiliary_id");
         capabilities = js.getLong("capabilities");
         stat         = js.getInt("stat");
         virtualip    = js.getString("virtualip");
         flows        = js.getInt("flows");
     }
     public static void clear()
     {
    	 try
    	 {
		    Connection conn = DBconn.createConnection();
	        Statement stmt = conn.createStatement();
		    String sql = "delete from h_switch;";
		    stmt.executeUpdate(sql);
    	 }
    	 catch (Exception e)
    	 {
    		 e.printStackTrace();
    	 } 
    	 OFPPort.clear();
     }
     private void getDefaultSwitchInfo(JSONObject jo)
     {
     	jo.put("id", 0);
     	jo.put("n_buffer", 256);
     	jo.put("n_tables", 256);
     	jo.put("auxiliary_id", 0);
     	jo.put("capabilities", 71);
     	jo.put("flows", 0);
     }
     public JSONObject toJSONObject()
     {
    	 JSONObject js = new JSONObject();
    	 js.put("id", id);
    	 js.put("dpid", dpid);
    	 
    	 return js;
    	 
     }
     public void save()
     {
    	stat = HydraConst.OVS_STD;
 		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into"
					+ " h_switch(dpid,n_buffer,n_tables,auxiliary_id,capabilities,stat,flows,virtualip, update_time) "
					+ " values(%d, %d, %d, %d, %d, %d, %d, inet_aton('%s'),now());",
					dpid,n_buffer,n_tables,auxiliary_id,capabilities,stat,flows,virtualip);
			String sql = fat.toString();
			//System.out.println(sql);
			fat.close();
			stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
                this.id = (Long)rs.getObject(1);
            else
            	this.id = 0;
			//System.out.println(sql);			
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	 
     }

     public void update()
     {
  		try {
 			Connection conn = DBconn.createConnection();
 			Statement stmt = conn.createStatement();
 			Formatter fat = new Formatter();
 			String masterID = masterController != null ? "" +masterController.getId() : "NULL";
 			String slaveID = slaveController   != null ? "" + slaveController.getId() : "NULL";
 			fat.format("update h_switch "
 					+ " set n_buffer=%d,n_tables=%d,auxiliary_id=%d,capabilities=%d,stat=%d,flows=%d,"
 					+ " master_controller=%s,slave_controller=%s,update_time=now()"
 					+ " where dpid = %d;", n_buffer, n_tables, auxiliary_id, capabilities, stat, flows, 
 					  masterID, slaveID, dpid);
 			String sql = fat.toString();
 			fat.close();
 			stmt.executeUpdate(sql);
 			//System.out.println(sql);			
 			//rs.close();
 			stmt.close();
 			
 		} catch (SQLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}    	 
     }
	public long getIncrementid() {
		generation_id++;
		return generation_id;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getDpid() {
		return dpid;
	}
	public void setDpid(long dpid) {
		this.dpid = dpid;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
	public List<OFPPort> getPortList() {
		return pl;
	}
	public void setPortList(List<OFPPort> pl) {
		this.pl = pl;
	}
	public int getFlow() {
		return flows;
	}
	public void setFlow(int flows) {
		this.flows = flows;
	}
	public String getVirtualip() {
		return virtualip;
	}
	public void setVirtualip(String virtualip) {
		this.virtualip = virtualip;
	}

    public long getGeneration_id() {
		return generation_id;
	}
	public void setGeneration_id(long generation_id) {
		this.generation_id = generation_id;
	}
	public HController getSlaveController() 
	{
		return slaveController;
	}
	public void setSlaveController(HController slaveController) 
	{
		this.slaveController = slaveController;
	}
	public HController getMasterController() 
	{
		return masterController;
	}
	public void setMasterController(HController masterController) 
	{
		this.masterController = masterController;
	}
	public String getMasterRestUrl()
	{
		if(masterController != null)
		    return masterController.getRestUrl();
		else
			return null;
	}
	public String getSlaveRestUrl()
	{
		if(slaveController != null)
		    return slaveController.getRestUrl();
		else
			return null;
	}

     
}
