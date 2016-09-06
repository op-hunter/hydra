package cn.edu.njnet.hydra.conf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class NodeConfig {
	private final HashMap<String,String> nodeMap;
	private static NodeConfig instance;

	private NodeConfig(HashMap<String,String> hs)
	{
		nodeMap = hs;
	}
	public String getNodeConfig(String str)
	{
		return nodeMap.get(str);
	}
	public static NodeConfig getNodeConfig()
	{
		if(instance == null)
		{
		   synchronized(NodeConfig.class)
		   {
			  if(instance == null)
			  {
				 instance = buildInstance();
			  }
		   }
		}
		return instance;	  
	}
	private static NodeConfig buildInstance()
	{
		try
		{    		
			BufferedReader buff = new BufferedReader(new FileReader(ConstConf.nodeConf));
			String str;
			str = buff.readLine();
			HashMap<String,String> hs = new HashMap<String,String>();
			while(str  != null)
			{
				String[] strs = str.split("[ ]+");
				hs.put(strs[0], strs[1]);
				str = buff.readLine();	
			}
			NodeConfig nc = new NodeConfig(hs);
			buff.close();
			return nc;
		}
		catch(Exception e)
		{
			return null;
		}
	}

}
