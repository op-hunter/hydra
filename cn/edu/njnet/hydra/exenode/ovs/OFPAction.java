package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

import org.json.JSONObject;

import cn.edu.njnet.hydra.dbconn.DBconn;

public class OFPAction {
	private Long ID;
    private HAction type;
    private Integer port;
    private OFPInst inst;
    public OFPAction()
    {
    	
    }
    public OFPAction(JSONObject js)
    {
    	
    }
    
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	public HAction getType() {
		return type;
	}
	public void setType(HAction type) {
		this.type = type;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}

	
    public OFPInst getInst() {
		return inst;
	}
	public void setInst(OFPInst inst) {
		this.inst = inst;
	}
	
	
	public JSONObject toJSONObject() 
	{
		if(type == HAction.DROP)
		    return new JSONObject();
		else if(type == HAction.OUTPUT)
		{
			JSONObject jo = new JSONObject();
			jo.put("type","OUTPUT");
			jo.put("port",port);
			return jo;
		}
		return null;
	}
	
	
	public void save()
    {
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into h_flow_action(action_type,action_arg,inst) "
					+ " values(%d, %d, %d);",
					   this.type.value(),this.port,this.inst.getID());
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
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
}
