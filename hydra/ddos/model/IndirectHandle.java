package hydra.ddos.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.conf.NodeConfig;
import cn.edu.njnet.hydra.exenode.ovs.JobTable;
import cn.edu.njnet.hydra.service.JobService;
import cn.edu.njnet.hydra.service.NBOSService;
import cn.edu.njnet.hydra.service.UFlowService;
import hydra.ddos.pojo.AttackFlow;
import hydra.ddos.pojo.IndirectDDoSEntry;
import hydra.ddos.pojo.IndirectDDoSList;
import hydra.ddos.service.DDoSAutomateService;
import hydra.ddos.service.DDoSFlowService;

public class IndirectHandle {
	@Resource
	private NBOSService nbosService;
	@Resource
	private UFlowService uFlowService;
	@Resource
	private NodeConfig nodeConfig;
	@Resource
	private JobService jobService;
	@Resource
	private DDoSFlowService ddosFlowService;
	@Resource
	private DDoSAutomateService ddoSAutomateService;
	
	private HashMap<String,IndirectDDoSEntry> ddosResponse;//响应列表
	private HashMap<String,Integer> ddosResponseLast;
	
	private HashMap<String,Integer> ddosWatch;//观察列表
	private HashSet<String> ddosWatchLast;//观察列表

	
	private int responseAgeing = 2;
	private long responseThrePPS;
	private long responseThreKBPS;
	private long watchThrePPS;
	private long watchThreKBPS;
	private int watchTimeThre;
    private int ruleThre = 200;
    
	public IndirectHandle()
	{
		ddosResponse = new HashMap<String,IndirectDDoSEntry>();
		ddosWatch = new HashMap<String,Integer>();
		ddosResponseLast = new HashMap<String,Integer>();
	}
	/**
	 * 在初始化方法中加载阈值
	 */
	public void init()
	{
		responseThrePPS = (long) (Long.valueOf(nodeConfig.getNodeConfig("L2_MAX_KPPS")) * 1024 *
				Double.valueOf(nodeConfig.getNodeConfig("DDOS_RESPONSE_THRESHOLD")));
		responseThreKBPS = (long) (Long.valueOf(nodeConfig.getNodeConfig("L2_MAX_MBPS")) * 1024 *
				Double.valueOf(nodeConfig.getNodeConfig("DDOS_RESPONSE_THRESHOLD")));
	    watchThrePPS = (long) (Long.valueOf(nodeConfig.getNodeConfig("L2_MAX_KPPS")) * 1024 *
	    		Double.valueOf(nodeConfig.getNodeConfig("DDOS_WATCH_THRESHOLD")));
	    watchThreKBPS = (long) (Long.valueOf(nodeConfig.getNodeConfig("L2_MAX_MBPS")) * 1024 *
	    		Double.valueOf(nodeConfig.getNodeConfig("DDOS_WATCH_THRESHOLD")));
	    watchTimeThre = Integer.valueOf(nodeConfig.getNodeConfig("WATCH_TIME_THRESHOLD"));
	}
	/**
	 * L2 响应模型
	 * @param currentDirectList
	 */
	public void preventDDoS(IndirectDDoSList currentDirectList) {
		ddosWatchLast = new HashSet<String>();
		List<IndirectDDoSEntry> entries = currentDirectList.getIndirectDDoSEntries();		
		for(IndirectDDoSEntry currentIndirectEntry : entries)
		{
			String location = currentIndirectEntry.getAccessSchool();
			if(ddosResponse.containsKey(location))
			{
				updateAttackFlow(currentIndirectEntry);
				ddosResponseLast.put(location, responseAgeing);
				continue ;
			}
			if(currentIndirectEntry.getMaxPPS() > responseThrePPS || 
					currentIndirectEntry.getMaxKBPS() > responseThreKBPS)
			{
				if(ddosWatch.containsKey(location))
				{
					ddosWatch.remove(location);
				}
				addResponseList(currentIndirectEntry);
				ddosResponseLast.put(location, responseAgeing);
				continue;
			}
			if(currentIndirectEntry.getMaxPPS() > watchThrePPS || 
					currentIndirectEntry.getMaxKBPS() > watchThreKBPS)
			{
				if(ddosWatch.containsKey(location))
				{
					int watchTime = ddosWatch.get(location);
					if(watchTime >= watchTimeThre)
					{
						ddosWatch.remove(location);
						addResponseList(currentIndirectEntry);
						ddosResponseLast.put(location, responseAgeing);
					}
					else
					{
						ddosWatch.put(location, watchTime+1);
						ddosWatchLast.add(location);
					}
				}
				else
				{
					ddosWatch.put(location, 1);
					ddosWatchLast.add(location);
				}
			}
		}
		refresh();	
	}
	private void refresh() {
		refreshWatchList();
		refreshResponseList();		
	}
	/**
	 * 变更为响应状态，生成对应的流表项
	 * @param currentIndirectEntry
	 */
	private void addResponseList(IndirectDDoSEntry currentIndirectEntry) {
        buildResponseJob(currentIndirectEntry);
		buildAttackFlow(currentIndirectEntry);
		ddoSAutomateService.commitL2DDoSAutomate(currentIndirectEntry);
		ddosFlowService.commitL2AttackResponse(currentIndirectEntry);	
		
	}
	/**
	 * 
	 * @param indirectDDoSEntry
	 */
	private void deleteResponseList(IndirectDDoSEntry indirectDDoSEntry) {
		ddoSAutomateService.cancelL2DDoSAutomate(indirectDDoSEntry);
		ddosFlowService.cancelL2AttackResponse(indirectDDoSEntry);	
	}
	/**
	 * 创建响应作业
	 * @param currentIndirectEntry
	 */
	private void buildResponseJob(IndirectDDoSEntry currentIndirectEntry)
	{
		String l2Name = currentIndirectEntry.getAccessSchool();
		JobTable ja = new JobTable();
		ja.setName("L2_DDoS响应_"+ l2Name);
		ja.setType(HydraConst.TABLE_RUN);
		ja.setAutomate(HydraConst.TABLE_AUTO);
		jobService.saveJobTable(ja);
		currentIndirectEntry.setResponseJob(ja);
	}
	/**
	 * 状态转换图中的5，目前采用了早期的方案，连续两次检测不到就移出
	 */
	private void refreshResponseList() {
		ArrayList<String> tobeDelete = new ArrayList<String>();
		Iterator<Entry<String, IndirectDDoSEntry>>  iterator = ddosResponse.entrySet().iterator();
		while(iterator.hasNext())
		{
			Entry<String, IndirectDDoSEntry> temEntry = iterator.next();
			String loc = temEntry.getKey();
			if(ddosResponseLast.containsKey(loc))
			{
				int flag = ddosResponseLast.get(loc);
				flag--;
				if(flag == 0)
					tobeDelete.add(loc);
				else
					ddosResponseLast.put(loc, flag);
			}
			else
			{
				tobeDelete.add(loc);
			}
		}
		for(String key : tobeDelete)
		{
			deleteResponseList(ddosResponse.get(key));
			ddosResponse.remove(key);
			ddosResponseLast.remove(key);
			ddosWatch.put(key, 1);  //退回观察列表
		}
	}
	/**
	 * 状态转换图中的4，将本时间粒度没有检测到的攻击移出观察列表
	 */
	private void refreshWatchList() {
		ArrayList<String> tobeDelete = new ArrayList<String>();
		Iterator<Entry<String, Integer>>  iterator = ddosWatch.entrySet().iterator();
		while(iterator.hasNext())
		{
			Entry<String, Integer> entry = iterator.next();
			String temKey = entry.getKey();
			if(!ddosWatchLast.contains(temKey))
			{
				tobeDelete.add(temKey);
			}
		}
		for(String loc : tobeDelete)
		{
			ddosWatch.remove(loc);
		}
	}

	/**
	 * 生成阻断攻击流,如果攻击流的数量大于ruleThre
	 * 那么就选前200条攻击流   仅仅是目前的短期方法
	 * @param currentIndirectEntry
	 */
	private void buildAttackFlow(IndirectDDoSEntry currentIndirectEntry) {
		List<AttackFlow> attackFlowList = currentIndirectEntry.getAttackFlowList();
		if(attackFlowList.size() >= ruleThre)
		{
			List<AttackFlow> responseFlowList = new ArrayList<AttackFlow>();
			for(int i = 0 ;i < ruleThre-1;i++)
			{
				responseFlowList.add(attackFlowList.get(i));
			}
		}
		else
		{
			List<AttackFlow> responseFlowList = attackFlowList;
			currentIndirectEntry.setResponseFlowList(responseFlowList);
		}
		
	}
	/**
	 * 更新流表项,暂时未实现
	 * @param currentDirectEntry
	 */
	private void updateAttackFlow(IndirectDDoSEntry currentDirectEntry) {

		
	}

}
