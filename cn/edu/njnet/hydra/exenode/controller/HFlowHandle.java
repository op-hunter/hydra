package cn.edu.njnet.hydra.exenode.controller;

import java.util.ArrayList;
import java.util.List;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.conf.NodeConfig;
import cn.edu.njnet.hydra.exenode.ovs.HAction;
import cn.edu.njnet.hydra.exenode.ovs.OFPAction;
import cn.edu.njnet.hydra.exenode.ovs.OFPFlow;
import cn.edu.njnet.hydra.exenode.ovs.OFPInst;
import cn.edu.njnet.hydra.exenode.ovs.OFPMatch;
import cn.edu.njnet.hydra.exenode.ovs.UAction;
import cn.edu.njnet.hydra.exenode.ovs.UFlow;

public class HFlowHandle {
 
	private UFlow uf;
	private ArrayList<OFPFlow> al;
	private ArrayList<OFPMatch> ms;
	public HFlowHandle(UFlow uf)
	{
    	al = new ArrayList<OFPFlow>();
    	ms = new ArrayList<OFPMatch> ();
    	this.uf = uf;
	}
    public List<OFPFlow> uflowOFPFlow()
    {

    	createMatch();
    	
    	createOFPFlow();
    	return al;  
    }
    private void createOFPFlow()
    {
    	for(OFPMatch mt : ms)
    	{
    		OFPFlow fl = new OFPFlow();
    		fl.setMatch(mt);
    		OFPInst inst = new OFPInst();
    		inst.setActions(createAction()); 
    		fl.setInst(inst);
    		fl.setPriority(2222);
    		/*
    		 * cookie前32位是JID，后32位是UF ID
    		 */
    		long cookie = uf.getJID();
    		cookie <<= 32;
    		fl.setCookie_mask(cookie);
    		cookie += uf.getID();
    		fl.setCookie(cookie);
    		
    		al.add(fl);
    	}
    }
    /*
     * Ŀǰ����������⣬���Ǽ�һ�ִ���
     */
    private List<OFPAction> createAction()
    {
    	List<OFPAction> res = new ArrayList<OFPAction>();
    	if(uf.getUAct() == UAction.DROP)
    	{
    		OFPAction hact = new OFPAction();
    		hact.setType(HAction.DROP);
    		res.add(hact);
    		return res;
    	}
    	else if(uf.getUAct() == UAction.FORWORD)//ĿǰFORWORD��1�ſڳ���CAPTURE��2�ſڳ�
    	{
    		OFPAction hact = new OFPAction();
    		hact.setType(HAction.OUTPUT);
    		hact.setPort(1);
    		res.add(hact);
    		return res;
    	}
    	else if(uf.getUAct() == UAction.CAPTURE)
    	{
    		OFPAction hact = new OFPAction();
    		hact.setType(HAction.OUTPUT);
    		int port = Integer.valueOf(NodeConfig.getNodeConfig().getNodeConfig("CONST_PORT"));
    		hact.setPort(port);//const
    		res.add(hact);    		
    		return res;
    	}
    	else
    	{
    		return res;
    	}
    }
    private void createMatch()
    {
    	OFPMatch mt = new OFPMatch();
		
    	if(uf.getIpProto() >=0)
    	{
    		mt.setIP_PROTO(uf.getIpProto());
    	}
    	ms.add(mt);
    	ipMatch();
    	portMatch();
    }
    private void ipMatch()
    {
		int l = ms.size();
		for (int i = 0; i < l; i++) 
		{
			OFPMatch mt = ms.get(i);

			if (uf.getIpPattern() == HydraConst.PATTERN_AND) {
				mt.setIPV4_SRC(uf.getIpv4_src());
				mt.setSrc_mask(uf.getSrc_mask());

				mt.setIPV4_DST(uf.getIpv4_dst());
				mt.setDst_mask(uf.getDst_mask());
			}
			if (uf.getIpPattern() == HydraConst.PATTERN_OR) {
				mt.setIPV4_SRC(uf.getIpv4_src());
				mt.setSrc_mask(uf.getSrc_mask());

				mt.setIPV4_DST(uf.getIpv4_dst());
				mt.setDst_mask(uf.getDst_mask());
				ms.add(mt.cloneMatch());

				mt.setIPV4_SRC(uf.getIpv4_dst());
				mt.setSrc_mask(uf.getDst_mask());

				mt.setIPV4_DST(uf.getIpv4_src());
				mt.setDst_mask(uf.getSrc_mask());
			}
		}
    }
    private void portMatch()
    {
		int l = ms.size();

		for (int i = 0; i < l; i++) 
		{
			OFPMatch mt = ms.get(i);
			if (uf.getPort_pattern() == HydraConst.PATTERN_AND) 
			{
				if (uf.getIpProto() == HydraConst.TCP) 
				{
					mt.setTCP_DST(uf.getDst_port());
					mt.setTCP_SRC(uf.getSrc_port());
				} 
				else if(uf.getIpProto() == HydraConst.UDP)
				{
					mt.setUDP_SRC(uf.getSrc_port());
					mt.setUDP_DST(uf.getDst_port());
				}
			} 
			else if (uf.getPort_pattern() == HydraConst.PATTERN_OR) 
			{
				if (uf.getIpProto() == HydraConst.TCP) 
				{
					mt.setTCP_DST(uf.getDst_port());
					mt.setTCP_SRC(uf.getSrc_port());
					ms.add(mt.cloneMatch());

					mt.setTCP_DST(uf.getSrc_port());
					mt.setTCP_SRC(uf.getDst_port());
					
				} 
				else if (uf.getIpProto() == HydraConst.UDP) 
				{
					mt.setUDP_DST(uf.getDst_port());
					mt.setUDP_SRC(uf.getSrc_port());
					ms.add(mt.cloneMatch());

					mt.setUDP_DST(uf.getSrc_port());
					mt.setUDP_SRC(uf.getDst_port());
					
				}
			}
		}
    }
}
