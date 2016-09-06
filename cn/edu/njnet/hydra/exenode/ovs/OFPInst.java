package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.njnet.hydra.dbconn.DBconn;

public class OFPInst 
{
	private Long ID;
    private List<OFPAction> actions;
    private OFPFlow flow;
    
    public OFPInst(JSONObject js)
    {
    	actions = new ArrayList<OFPAction>();
    }
    public OFPInst()
    {
    	actions = new ArrayList<OFPAction>();
    }
    
    public OFPFlow getFlow() {
		return flow;
	}
	public void setFlow(OFPFlow flow) {
		this.flow = flow;
	}
	public List<OFPAction> getActions() {
		return actions;
	}
	public void setActions(List<OFPAction> _actions) {
		this.actions = _actions;
		for(OFPAction act : actions)
		{
			act.setInst(this);
		}
	}
	public void addActions(OFPAction act)
	{
		actions.add(act);
	}
	
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	public JSONArray toJSONObject()//
    {
    	JSONArray jas = new JSONArray();
    	for(OFPAction act : actions)
    	{
    		if(act.getType() != HAction.DROP)
    		{
    		   jas.put(act.toJSONObject());
    		}
    	}
    	//res.put("actions", jas);
    	return jas;
    }
	
	public List<OFPInst> getAllInst(int u_id)
	{
		return null;
	}
	public void save() 
	{
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into h_flow_inst(inst_type,h_flow_id) "
					+ " values( %d, %d);",0,flow.getID());
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
                this.ID = (Long)rs.getObject(1);
            else
            	this.ID = null;
			System.out.println(sql);
			for(OFPAction act : actions)
			{
				act.setInst(this);
				act.save();
			}
			rs.close();
			stmt.close();

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	public static void main(String[] args) 
	{
		OFPInst inst = new OFPInst();
		OFPAction a1 = new OFPAction();
		a1.setType(HAction.OUTPUT);
		a1.setPort(3);
		inst.addActions(a1);
		OFPAction a2 = new OFPAction();
		a2.setType(HAction.OUTPUT);
		a2.setPort(2);
		inst.addActions(a2);
		System.out.println(inst.toJSONObject().toString());
	}
    
}
