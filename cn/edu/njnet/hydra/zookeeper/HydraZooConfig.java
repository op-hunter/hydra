package cn.edu.njnet.hydra.zookeeper;

import org.apache.zookeeper.data.Stat;
import org.json.JSONArray;
import cn.edu.njnet.hydra.conf.NodeConfig;

public class HydraZooConfig {

	private NodeConfig nodeConfig;
	
    private int SESSION_TIMEOUT; 
    private String CONNECTION_STRING;  
    private String rootPath;    
    private String exeNodePath;
    
    private ZooApi sample;
    private Stat st;
    
    private String baseSwitchPath;
    private String controllerPath;
    
	
    private ConfigChangeWatch baseSwitchWa;
    private ConfigChangeWatch controllerPathWa;
        
    private static HydraZooConfig instance;
    
    private HydraZooConfig()
    {

    }
    private void init()
    {
    	nodeConfig        = NodeConfig.getNodeConfig();
    	CONNECTION_STRING = nodeConfig.getNodeConfig("ZOO_CONNECTION_STRING");
    	SESSION_TIMEOUT   = Integer.valueOf(nodeConfig.getNodeConfig("SESSION_TIMEOUT"));
    	exeNodePath       = nodeConfig.getNodeConfig("ZOO_EXE_NODE_PATH");
    	
    	baseSwitchPath    = exeNodePath + "/baseSwitch";
        controllerPath    = exeNodePath + "/controller";
        
        baseSwitchWa     = new ConfigChangeWatch(baseSwitchPath,sample);
        controllerPathWa = new ConfigChangeWatch(controllerPath,sample);
    	
    	instance.st = new Stat();
    	instance.sample = new ZooApi(); 
    	instance.sample.createConnection( instance.CONNECTION_STRING, instance.SESSION_TIMEOUT );
    }
    public static HydraZooConfig getZooConfig()
    {
		if(instance == null)
		{
	        synchronized(HydraZooConfig.class){
	    	   if(instance == null)
	           {
	    		   try
	    		   {
	    		      instance = new HydraZooConfig();
	    		      instance.init();
	    		   }
	    		   catch(Exception e)
	    		   {
	    			   e.printStackTrace();
	    			   instance = null;
	    		   }
	    	   }
	        }
		}
		return instance;
    }
	public String getHydraVersion()
    {
        return sample.readData(rootPath); 
    }
	
	public JSONArray getBaseSwitch()
	{
		String str = sample.readData(baseSwitchPath, baseSwitchWa, st);
		if(str != null)
		   return new JSONArray(str);
		else
		   return new JSONArray();
	}
	public void addBaseSwitchWa(ZooCallBack zcb)
	{
		baseSwitchWa.addCallBack(zcb);
	}
	
	public JSONArray getController()
	{
		String str = sample.readData(controllerPath, baseSwitchWa, st);
		if(str != null)
		   return new JSONArray(str);
		else
		   return new JSONArray();		
	}
	public void addControllerWa(ZooCallBack zcb)
	{
		controllerPathWa.addCallBack(zcb);
	}
    public void release()
    {
    	sample.releaseConnection();
    }
}
