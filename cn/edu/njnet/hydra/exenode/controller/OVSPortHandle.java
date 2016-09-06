package cn.edu.njnet.hydra.exenode.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.njnet.hydra.exenode.ovs.OFPPort;
import cn.edu.njnet.hydra.exenode.ovs.OFPPortStat;
import cn.edu.njnet.hydra.exenode.ovs.OFPSwitch;

public class OVSPortHandle {

	public void updatePortStat(OFPSwitch sw,JSONArray ja)
	{
		List<OFPPort> pl = sw.getPortList();
		if(pl == null)
		{
			pl = new ArrayList<OFPPort>();
			sw.setPortList(pl);
		}
		int l = ja.length();
		boolean[] flags = new boolean[l];
		for(int i = 0; i < l;i++)
		{
			JSONObject jo = ja.getJSONObject(i);	
			Long pn = jo.getLong("port_no");
			int pll = pl.size();
			for(int j = 0;j<pll;j++)
			{
				if(pn == pl.get(j).getPort_no())
				{
					pl.get(j).getLastStat().updateStat(jo,sw.getId());
					flags[i] = true;
				}
			}
		}
		for(int x = 0;x < l;x++)
		{
			if(flags[x] == false)
			{
				OFPPort port = new OFPPort();
				JSONObject jo = ja.getJSONObject(x);
				OFPPortStat portStat = new OFPPortStat(port);
				port.setSw(sw);
				port.setPort_no(jo.getLong("port_no"));;
				port.setLastStat(portStat);
				pl.add(port);
				portStat.updateStat(jo,sw.getId());
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
