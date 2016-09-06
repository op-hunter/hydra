package cn.edu.njnet.hydra.rest;

import cn.edu.njnet.hydra.exenode.ovs.HController;
import cn.edu.njnet.hydra.exenode.ovs.OFPSwitch;

public class HydraRes {


	public static String getMasterRestInfo(OFPSwitch sw, String method, String url,
			String args) {
		ResponseCode code = new ResponseCode();
		String hostUrl = sw.getSlaveRestUrl();
		if(hostUrl == null)
			return null;
		String str = HydraRestRequestUtil.httpConnection(method, hostUrl, url, args, code);
		if (code.getValue() == 0) {
			return null;
		} else
			return str;

	}

	public static String getAuxRestInfo(OFPSwitch sw, String method, String url, String args) {
		ResponseCode code = new ResponseCode();
		String hostUrl = sw.getSlaveRestUrl();
		if(hostUrl == null)
			return null;
		String str = HydraRestRequestUtil.httpConnection(method, hostUrl, url, args, code);
		if ( code.getValue() == 0 ) {
			return null;
		} else
			return str;
	}

	public static String getDefaultRestInfo(OFPSwitch sw, String method, String url, String args) {
		ResponseCode code = new ResponseCode();
		String hostUrl = sw.getMasterRestUrl();
		if(hostUrl == null)
		{
			return null;
		}
		String str = HydraRestRequestUtil.httpConnection(method, hostUrl, url, args, code);
		//System.out.println(str);
		if ( code.getValue() == 0 ) {
			return null;
		} 
		else
			return str;
	}

	public static String httpConnection(String method, HController hc, String url,
			String args,ResponseCode code) {
		String str = HydraRestRequestUtil.httpConnection(method, hc.getRestUrl(), url, 
				args, code);
		if ( code.getValue() == 0 ) {
			return null;
		} 
		else
			return str;
	}
	
}
