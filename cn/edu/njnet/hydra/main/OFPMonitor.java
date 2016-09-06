package cn.edu.njnet.hydra.main;

import java.util.List;

import javax.annotation.Resource;

import cn.edu.njnet.hydra.exenode.controller.ConSwHandle;
import cn.edu.njnet.hydra.exenode.controller.ControllerHandle;
import cn.edu.njnet.hydra.exenode.controller.ExeNodeHandle;
import cn.edu.njnet.hydra.exenode.controller.OVSHandle;
import cn.edu.njnet.hydra.exenode.controller.OVSPortHandle;
import cn.edu.njnet.hydra.exenode.controller.UFlowHandle;
import cn.edu.njnet.hydra.exenode.ovs.UFlow;
import cn.edu.njnet.hydra.zookeeper.HydraZooConfig;

public class OFPMonitor implements Runnable 
{
	@Resource
	private OVSHandle ovsHandle;
	@Resource
	private UFlowHandle uFlowHandle;
	@Resource
	private OVSPortHandle ovsPortHandle;
	@Resource
	private ControllerHandle controllerHandle;
	@Resource
	private ConSwHandle conSwHandle;
	@Resource
	private HydraZooConfig hydraZooConfig;
	@Resource
	private ExeNodeHandle exeNodeHandle;
	
	public OFPMonitor()
	{
		
	}
	public void init()
	{
		hydraZooConfig.addBaseSwitchWa(ovsHandle);
		hydraZooConfig.addControllerWa(controllerHandle);
		
		controllerHandle.initControllerList();
		ovsHandle.initSws();
		
		conSwHandle.updateConSwMap();
		ovsHandle.clearAllSwitch();
	}
	@Override
	public void run() 
	{
		while(true)
		{
			try 
			{	
			    conSwHandle.updateConSwMap();
			    exeNodeHandle.updateExeNodeStat();

			    List<UFlow> ufa = uFlowHandle.getToAddUserflow();
			    ovsHandle.addUFlowToAllSwitch(ufa);
			    uFlowHandle.updateAddUserflow();
			
			    List<UFlow> ufd = uFlowHandle.getToDeleteflow();
			    ovsHandle.deleteUFlowToAllSwitch(ufd);
			    uFlowHandle.deleteUserflow();
			
			    ovsHandle.UpdateUFlowStatus();		   
			    ovsHandle.updatePortStat();		   

			}
			catch (Exception e) {
				e.printStackTrace();
			}
            try {
				Thread.sleep(1000);
			} catch (InterruptedException interException) {
				interException.printStackTrace();
			}
		}
	}
    public void clear()
    {
    	ovsHandle.clearAllSwitch();
    }	
}
