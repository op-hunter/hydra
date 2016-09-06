package cn.edu.njnet.hydra.exenode.controller;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.conf.RestUrlMap;
import cn.edu.njnet.hydra.dbconn.DBconn;
import cn.edu.njnet.hydra.exenode.ovs.ControllerRole;
import cn.edu.njnet.hydra.exenode.ovs.HController;
import cn.edu.njnet.hydra.exenode.ovs.OFPSwitch;
import cn.edu.njnet.hydra.rest.HydraRes;
import cn.edu.njnet.hydra.rest.HydraRestRequestUtil;
import cn.edu.njnet.hydra.rest.ResponseCode;
import cn.edu.njnet.hydra.zookeeper.HydraZooConfig;
import cn.edu.njnet.hydra.zookeeper.ZooCallBack;

public class ControllerHandle implements ZooCallBack {

	@Resource
	private HydraZooConfig hydraZooConfig;
	@Resource
	private ConSwHandle conSwHandle;
	@Resource
    private RestUrlMap urlMap;
	@Resource
	private RestUrlMap restUrlMap;
	
	private List<HController> conLists;
	
	public ControllerHandle() {
		
	}
	public void initControllerList() {
		conLists = new ArrayList<HController>();
		clear();//清除h_controller这张表
		JSONArray conArray = hydraZooConfig.getController();	
		int conCount = conArray.length();
		for(int i = 0; i < conCount;i++)
		{
			HController hc = new HController(conArray.getJSONObject(i));
			hc.setStat(HydraConst.CONTROLLER_STD);
			conLists.add(hc);
			hc.save();
		}
		
	}
	public void changeControllerList()
	{
		JSONArray conArray = hydraZooConfig.getController();	
		int conCount = conArray.length();
		for(int i = 0; i < conCount;i++)
		{
			HController hc = new HController(conArray.getJSONObject(i));
			hc.setStat(HydraConst.CONTROLLER_STD);
			conLists.add(hc);
			hc.update();
		}		
	}
	/**
	 * 调用GetControllerStat接口
	 */
	public void updateControllerStat() {
		ResponseCode code = new ResponseCode();
		for(HController hc: conLists)
		{
			String str = HydraRestRequestUtil.httpConnection(urlMap.getMethod("GetControllerStat"), 
					hc.getRestUrl(), urlMap.getURL("GetControllerStat"), null, code);
			if(str != null && code.getValue() == 200)
			{
				hc.setStat(HydraConst.CONTROLLER_STD);
				JSONObject js = new JSONObject(str);
				hc.updateStat(js);
			}
			else
			{
				hc.setStat(HydraConst.CONTROLLER_ERROR);
				hc.clearStat();
			}
			hc.update();
		}	
	}
	public static void clear() {
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			String sql = "delete from h_controller;";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public List<HController> getConLists() {
		return conLists;
	}
	public void setConLists(List<HController> conLists) {
		this.conLists = conLists;
	}
	@Override
	public void CallBack() {
		changeControllerList();
	}
	public void getMasterController(OFPSwitch sw) {
		for(HController hc : conLists)
		{
			if(hc.getStat() == HydraConst.CONTROLLER_STD
					&& checkStat(sw,hc) == true)
			{
				selectMasterController(sw, hc);
			}
		}
	}
	public void getSlaveController(OFPSwitch sw) {
		if(sw.getMasterController() == null)
		{
			sw.setSlaveController(null);
			return ;
		}
		for(HController hc : conLists)
		{
			if(hc.getStat() == HydraConst.CONTROLLER_STD				
					&& hc != sw.getMasterController()
					&& checkStat(sw,hc) == true)
			{
				selectMasterController(sw, hc);
			}
		}
	}
	private boolean checkStat(OFPSwitch sw, HController hc) 
	{
		if(hc == null)
			return false;
		ResponseCode code = new ResponseCode();
		String str = HydraRes.httpConnection(restUrlMap.getMethod("GetSwitchStat"), hc, 
				restUrlMap.getURL("GetSwitchStat") + sw.getDpid(), null, code);
    	if(str == null)
    		return false;
    	else
        	return true;
	}
	private void selectMasterController(OFPSwitch sw, HController controller)
    {
		boolean masterFlag = false;
		masterFlag = synController(sw, controller,ControllerRole.MASTER);
		if(masterFlag == true)
		{
			sw.setMasterController(controller);
		}
		else
		{
			sw.setMasterController(null);
		}
    }
	private boolean synController(OFPSwitch sw, HController hc,
			ControllerRole role) {
		boolean flags = true;
		String url = restUrlMap.getURL("StatRole")
				+ String.valueOf(sw.getDpid()) + "/"
				+ ControllerRole.NOCHANGE.value() + "/1";
		ResponseCode rescode = new ResponseCode();
		String res = HydraRes.httpConnection(restUrlMap.getMethod("StatRole"),
				hc, url, null, rescode);
		if (rescode.getValue() >= 500) {
			hc.setStat(HydraConst.CONTROLLER_ERROR);
			return false;
		}
		if (rescode.getValue() >= 400) {
			sw.setStat(HydraConst.OVS_DISCONNECT);
			return false;
		}
		if (res == null) {
			sw.setStat(HydraConst.OVS_DISCONNECT);
			return false;
		}
		JSONObject js = new JSONObject(res);
		if (js.has("dpid")) {
			flags = false;
			long gener_id = 0;
			try {
				gener_id = Long.valueOf(js.getString("generation_id"));
			} catch (NumberFormatException e) {
				gener_id = 1;
			}
			if (gener_id <= 0) {
				gener_id = 1;
			}
			sw.setGeneration_id(gener_id);
			setControllerRole(sw,hc,role);
			return true;
		}
		return flags;
	}
	private void setControllerRole(OFPSwitch sw, HController con,
			ControllerRole role) 
	{
		String url = restUrlMap.getURL("StatRole") + String.valueOf(sw.getDpid())
				+ "/" + role.value() + "/" + sw.getIncrementid();
		ResponseCode rescode = new ResponseCode();
		String res = HydraRes.httpConnection(restUrlMap.getMethod("StatRole"),
				con, url, null, rescode);
		JSONObject js = new JSONObject(res);
		if (js.has("dpid")) {
			long gener_id;
			try {
				gener_id = Long.valueOf(js.getString("generation_id"));
			} catch (NumberFormatException e) {
				gener_id = 1;
			}
			if (gener_id <= 0) {
				gener_id = 0;
				sw.setGeneration_id(1L);
				con.setStat(HydraConst.CONTROLLER_ERROR);
			} else {
				sw.setGeneration_id(gener_id);
				con.setStat(HydraConst.CONTROLLER_STD);
			}
		}
	}
}
