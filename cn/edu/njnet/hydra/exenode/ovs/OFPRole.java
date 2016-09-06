package cn.edu.njnet.hydra.exenode.ovs;

import org.json.JSONObject;

public class OFPRole {
	public static long sequence;
	public long generation_id;
	private ControllerRole role;
	private OFPSwitch sw;
	
	public OFPRole()
	{
		
	}
	public JSONObject getJSONObject()
	{
		JSONObject js = new JSONObject();
		return js;
	}
	public String getURLString()
	{
		String url = sw.getDpid() + "/" + role.value() + "/" + generation_id;
		return url;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
