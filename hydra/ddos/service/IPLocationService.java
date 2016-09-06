package hydra.ddos.service;

import hydra.ddos.pojo.HydraIPAddress;
import hydra.ddos.pojo.IPTrie;
import hydra.ddos.util.IPUtil;

import javax.annotation.Resource;

import cn.edu.njnet.hydra.conf.NodeConfig;

public class IPLocationService {
	@Resource
	private NodeConfig nodeConfig;
	
	private IPTrie ipTrie;
	
	private IPLocationService()
	{
		
	}
	public void init()
	{
		loadIPlocation();
	}
    public void getLocation(HydraIPAddress hydraIPAddress)
    {
    	ipTrie.getLocation(hydraIPAddress);
    }
    public String getLocation(long ipAddress)
    {
    	return ipTrie.getLocation(ipAddress);
    }
	private void loadIPlocation()
	{
		String filename = nodeConfig.getNodeConfig("IP_LOCATION_FILE");
		ipTrie = IPUtil.buildIPTrie(filename);
	}

}
