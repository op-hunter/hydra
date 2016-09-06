package cn.edu.njnet.hydra.exenode.controller;

import javax.annotation.Resource;

public class ConSwHandle {
	@Resource
	private OVSHandle ovsHandle;
	@Resource
	private ControllerHandle controllerHandle;
    
	/**
	 * 先更新控制器状态，然后更新交换机状态
	 * 2016-04-08 
	 */
	public void updateConSwMap()
	{
		controllerHandle.updateControllerStat();
		ovsHandle.updateSwitchStat();
	}
}
