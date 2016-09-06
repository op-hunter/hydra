package cn.edu.njnet.hydra.exenode.controller;

import javax.annotation.Resource;

import cn.edu.njnet.hydra.service.ExeNodeService;

public class ExeNodeHandle {
	@Resource
    public ExeNodeService exeNodeService;
	
	public void updateExeNodeStat()
	{
		exeNodeService.updateExeNodeStat();
	}
}
