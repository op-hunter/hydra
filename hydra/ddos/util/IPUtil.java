package hydra.ddos.util;

import hydra.ddos.pojo.HydraIPAddress;
import hydra.ddos.pojo.IPTrie;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import cn.edu.njnet.hydra.util.HydraUtil;

public class IPUtil {
    public static IPTrie buildIPTrie(String filename)
    {
    	try {
			InputStream input = new FileInputStream(filename);
			List<String> lines = IOUtils.readLines(input,"utf-8");
			IPTrie ipTrie = new IPTrie(); 
			for(String row : lines)
			{
				buildIPAddress(row,ipTrie);
			}
			return ipTrie;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
    }
    public static void buildIPAddress(String addressInfo, IPTrie ipTrie)
    {
    	HydraIPAddress hydraIPAddress = new HydraIPAddress();
    	String[] entrys = addressInfo.split("[\\|]");
    	String nodeid = entrys[14].trim();
    	if(nodeid.equals("10"))
    	{
        	String maskAddress = entrys[0].trim();
        	String[] maskAndAddress = maskAddress.split("/");
        	long ipAddress = HydraUtil.ip2long(maskAndAddress[0]);
        	int mask = Integer.valueOf(maskAndAddress[1]);
        	String locationID = entrys[16].trim();
        	String location = entrys[17].trim();
        	hydraIPAddress.setIPAddress(ipAddress);
        	hydraIPAddress.setIPMask(mask);
        	hydraIPAddress.setLocation(location);
        	hydraIPAddress.setLocationID(locationID);
        	ipTrie.insert(hydraIPAddress);
    	}    	
    }
}
