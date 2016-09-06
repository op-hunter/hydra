package cn.edu.njnet.hydra.conf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class RestUrlMap {
    
	private final HashMap<String,String> restUrl;
	private final HashMap<String,String> restMethod;
    private static RestUrlMap instance =null;
    
    private RestUrlMap(HashMap<String,String> hs,HashMap<String,String> hm)
    {
    	restUrl    = hs;
    	restMethod = hm;
    }
	public String getURL(String str)
	{
		return restUrl.get(str);
	}
	public String getMethod(String str)
	{
		return restMethod.get(str);
	}
    public static RestUrlMap getUrlMap()
    {
    	if(instance == null)
    	{
    		synchronized(RestUrlMap.class)
    		{
    			if(instance == null)
    			{
    				instance = buildRestUrlMap();
    			}
    		}
    	}
    	return instance;
    }
	private static RestUrlMap buildRestUrlMap() 
	{
		try
		{
			BufferedReader buff = new BufferedReader(new FileReader(ConstConf.urlConf));
			String str;
			str = buff.readLine();
			HashMap<String,String> hs = new HashMap<String,String>();
			HashMap<String,String> hm = new HashMap<String,String>();
			while(str  != null)
			{
				String[] strs = str.split("[ ]+");
				hs.put(strs[0], strs[2]);
				hm.put(strs[0], strs[1]);
				str = buff.readLine();	
			}
			buff.close();
			RestUrlMap rum = new RestUrlMap(hs,hm);			
			return rum;
		}catch(Exception e)
    	{
			return null;
    	}		
	}
}
