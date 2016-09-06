package cn.edu.njnet.hydra.exenode.controller;

/**
 * OVSHandle
 * 2016-01-04
 */
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.njnet.hydra.conf.*;
import cn.edu.njnet.hydra.exenode.ovs.*;
import cn.edu.njnet.hydra.rest.HydraRes;
import cn.edu.njnet.hydra.zookeeper.HydraZooConfig;
import cn.edu.njnet.hydra.zookeeper.ZooCallBack;

public class OVSHandle implements ZooCallBack
{  
	@Resource
    private RestUrlMap urlMap;
	@Resource
    private HydraZooConfig hydraZooConfig;
	@Resource
    private UFlowHandle ufh;
	@Resource
    private OVSPortHandle oph;
    @Resource
	private ControllerHandle controllerHandle;
    
    private List<OFPSwitch> sws;
    
    public OVSHandle()
    {
    	
    }
    public void initSws() { 
    	JSONArray ja = hydraZooConfig.getBaseSwitch();
    	ArrayList<OFPSwitch> swlist = new ArrayList<OFPSwitch>();
    	OFPSwitch.clear();
    	for(int i = 0; i < ja.length();i++)
    	{
    		JSONObject jn = (JSONObject) ja.get(i);
    		jn.put("stat", HydraConst.OVS_STD);
    		OFPSwitch ofps = new OFPSwitch(jn);
    		ofps.save();
    		swlist.add(ofps);      		
    	}
    	sws = swlist;
	}
    /**
     * 更新交换机状态
     */
	public void updateSwitchStat() {	
		for(OFPSwitch sw: sws)
		{
			if(sw.getMasterController() == null 
					|| sw.getStat() == HydraConst.OVS_DISCONNECT
					|| sw.getMasterController().getStat() == HydraConst.CONTROLLER_ERROR)
			{
				controllerHandle.getMasterController(sw);
			}
			if(sw.getSlaveController() == null || 
					sw.getSlaveController().getStat() == HydraConst.CONTROLLER_ERROR)
			{
				controllerHandle.getSlaveController(sw);
			}			
			if(sw.getMasterController() == null && sw.getSlaveController() == null)
				sw.setStat(HydraConst.OVS_DISCONNECT);
			sw.update();
		}
	}
	/*
     * 将流表添加到所有交换机
     */
    public void addFlowToAllSwitch(List<OFPFlow> flows)
    {
    	for(OFPSwitch i: sws)
    	{
    		if(i.getStat() == HydraConst.OVS_STD)
    		    addFlows(flows,i);
    	}
    }    
    private void addFlows(List<OFPFlow> flows, OFPSwitch sw)
    {
   	   for(OFPFlow flow:flows)
   	   {
   		   addFlow(flow,sw);
   	   }
    }
    /*
     * 将流表添加到交换机
     */
    private void addFlow(OFPFlow flow,OFPSwitch sw)
    {
    	JSONObject jo = flow.toJSONObject();
        jo.put("dpid", sw.getDpid());
   	    HydraRes.getDefaultRestInfo(sw, urlMap.getMethod("AddFlow"), urlMap.getURL("AddFlow"), jo.toString());
    }
    /*
     * 删除所有交换机上的流表
     */      
    public void deleteUFlowToAllSwitch(List<UFlow> ufl)
    {
		for(UFlow uf : ufl)
		{
			List<OFPFlow> hl = new HFlowHandle(uf).uflowOFPFlow();
			for(OFPSwitch sw : sws)
			{
				if(sw.getStat() == HydraConst.OVS_STD)
				{
					deleteFlows(hl, sw);
				}			
			}
		}    	
    }
    private void deleteFlows(List<OFPFlow> flows, OFPSwitch sw)
    {
   	   for(OFPFlow flow:flows)
       {
   		  deleteFlow(flow,sw);
   	   }    	 
    }
    /**
     * 从单个交换机上删除流表
     */
    private void deleteFlow(OFPFlow flow, OFPSwitch sw)
    {
    	JSONObject jo = flow.toJSONObject();
        jo.put("dpid", sw.getDpid());
   	    HydraRes.getDefaultRestInfo(sw, urlMap.getMethod("DeleteFlow"), urlMap.getURL("DeleteFlow"), jo.toString());
    }

    /**
     * 给所有属于该执行节点的交换机，添加流表
     */
    public void addUFlowToAllSwitch(List<UFlow> ufl)
    {
		for(UFlow uf : ufl)
		{
			for(OFPSwitch sw : sws)
			{
				if(sw.getStat() == HydraConst.OVS_STD)
				{
				   List<OFPFlow> hl = new HFlowHandle(uf).uflowOFPFlow();
				   addFlows(hl, sw);
				}
			}
		}    	
    }

    /*
     *
     */ 
    public void clearAllSwitch()
    {
    	for(OFPSwitch i: sws)
    	{
    		if(i.getStat() == HydraConst.OVS_STD)
    		{
    		   clearSwitch(i);
    		}
    	}         
    }
    public void clearSwitch(OFPSwitch sw)
    {
    	HydraRes.getDefaultRestInfo(sw, urlMap.getMethod("ClearFlow"), urlMap.getURL("ClearFlow")+sw.getDpid(), "");
    }
    /**
     * 
     */
    public void UpdateUFlowStatus()
    {
    	ufh.clearState();
        for(OFPSwitch sw : sws)
    	{
        	if(sw.getStat() == HydraConst.OVS_STD)
        	{
        	    String res = HydraRes.getDefaultRestInfo(sw, urlMap.getMethod("GetFlowStat"), urlMap.getURL("GetFlowStat")+sw.getDpid(), "");
        	    JSONObject json = new JSONObject(res);
    	        JSONArray jaflow = json.getJSONArray(String.valueOf(sw.getDpid()));
    	        sw.setFlow(jaflow.length());
    	        ufh.updateState(jaflow);
        	}
    	}
        ufh.updateStatToDb();
        ufh.updateLogStat();
    }
    /*
     *
     */
    public void updatePortStat()
    {
    	for(OFPSwitch sw : sws)
    	{
    	   String res = HydraRes.getDefaultRestInfo(sw, urlMap.getMethod("GetPortStat"), urlMap.getURL("GetPortStat")+sw.getDpid(), "");
    	   if(res != null)
    	   {
    	      JSONObject json = new JSONObject(res);
    	      JSONArray ja = json.getJSONArray(String.valueOf(sw.getDpid()));
    	      oph.updatePortStat(sw,ja);
    	   }
    	}
    }
    
    
	public List<OFPSwitch> getSws() {
		return sws;
	}
	public void setSws(List<OFPSwitch> sws) {
		this.sws = sws;
	}
	public UFlowHandle getUfh() {
		return ufh;
	}
	public void setUfh(UFlowHandle ufh) {
		this.ufh = ufh;
	}
	public OVSPortHandle getOph() {
		return oph;
	}
	public void setOph(OVSPortHandle oph) {
		this.oph = oph;
	}
	@Override
	public void CallBack() {
		initSws();
	}
}
