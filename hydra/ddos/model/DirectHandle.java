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
import cn.edu.njnet.hydra.util.HydraUtil;
import hydra.ddos.pojo.AttackFlow;
import hydra.ddos.pojo.AttackInfo;
import hydra.ddos.pojo.DirectDDoSEntry;
import hydra.ddos.pojo.DirectDDoSList;
import hydra.ddos.pojo.OppositeInfo;
import hydra.ddos.service.DDoSAutomateService;
import hydra.ddos.service.DDoSFlowService;

public class DirectHandle {
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
	
	private HashMap<Long,DirectDDoSEntry> ddosResponse;//响应列表
	private HashMap<Long,Integer> ddosResponseLast;
	
	private HashMap<Long,Integer> ddosWatch;//观察列表
	private HashSet<Long> ddosWatchLast;//观察列表
	
	private long responseThrePPS;
	private long responseThreKBPS;
	private long watchThrePPS;
	private long watchThreKBPS;
	private int  watchTimeThre;
    private int ruleThre = 200;
    
	public DirectHandle()
	{
		ddosResponse = new HashMap<Long, DirectDDoSEntry>();
		ddosResponseLast = new HashMap<Long,Integer>();
		ddosWatch = new HashMap<Long,Integer>();
	}
	/**
	 * 在初始化方法中加载阈值
	 */
	public void init()
	{
		responseThrePPS = (long) (Long.valueOf(nodeConfig.getNodeConfig("L1_MAX_KPPS")) * 1024 *
				Double.valueOf(nodeConfig.getNodeConfig("DDOS_RESPONSE_THRESHOLD")));
		responseThreKBPS = (long) (Long.valueOf(nodeConfig.getNodeConfig("L1_MAX_MBPS")) * 1024 *
		        Double.valueOf(nodeConfig.getNodeConfig("DDOS_RESPONSE_THRESHOLD")));
	    watchThrePPS = (long) (Long.valueOf(nodeConfig.getNodeConfig("L1_MAX_KPPS")) * 1024 *
	    		Double.valueOf(nodeConfig.getNodeConfig("DDOS_WATCH_THRESHOLD")));
	    watchThreKBPS = (long) (Long.valueOf(nodeConfig.getNodeConfig("L1_MAX_MBPS")) * 1024 *
	    		Double.valueOf(nodeConfig.getNodeConfig("DDOS_WATCH_THRESHOLD")));
	    watchTimeThre = Integer.valueOf(nodeConfig.getNodeConfig("WATCH_TIME_THRESHOLD"));
	}
	/**
	 * L1响应模型  具体流程可见论文图5-19
	 * @param currentDirectList
	 */
	public void preventDDoS(DirectDDoSList currentDirectList) {
		filterEntry(currentDirectList);
		ddosWatchLast = new HashSet<Long>();
		List<DirectDDoSEntry> entries = currentDirectList.getDDoSEntries();		
		for(DirectDDoSEntry currentDirectEntry : entries)
		{
			AttackInfo attackInfo = currentDirectEntry.getAttackInfo();
			long ddos_id = attackInfo.getDdos_id();
			if(ddosResponse.containsKey(ddos_id))
			{
				updateResponse(currentDirectEntry);
				continue ;
			}
			if(attackInfo.getMax_pps() > responseThrePPS || attackInfo.getMax_kbps() > responseThreKBPS)
			{
				if(ddosWatch.containsKey(ddos_id))
				{
					ddosWatch.remove(ddos_id);
				}
				addResponseList(currentDirectEntry);
				continue;
			}
			if(attackInfo.getMax_pps() > watchThrePPS || attackInfo.getMax_kbps() > watchThreKBPS)
			{
				if(ddosWatch.containsKey(ddos_id))
				{
					int watchTime = ddosWatch.get(ddos_id);
					if(watchTime >= watchTimeThre)
					{
						ddosWatch.remove(ddos_id);
						addResponseList(currentDirectEntry);
					}
					else
					{
						ddosWatch.put(ddos_id, watchTime+1);
						ddosWatchLast.add(ddos_id);
					}
				}
				else
				{
					ddosWatch.put(ddos_id, 1);
					ddosWatchLast.add(ddos_id);
				}
			}
		}
		refresh();
	}
	/**
	 * 可以实现对攻击流的过滤,比如对网外攻击的忽略，就是在此函数中完成的。
	 * @param currentDirectList
	 */
	private void filterEntry(DirectDDoSList currentDirectList) {
		List<DirectDDoSEntry> ddosEntryies =  currentDirectList.getDDoSEntries();
		List<DirectDDoSEntry> newDDoSEntryies = new ArrayList<DirectDDoSEntry>();
		for(DirectDDoSEntry entry:ddosEntryies)
		{
			if(((entry.getAttackInfo().getDdos_type()/16)%2) == 1)
				newDDoSEntryies.add(entry);
		}
		currentDirectList.setDDoSEntries(newDDoSEntryies);
	}
	/**
	 * 由于更新策略未定，暂时不更新攻击流列表
	 * @param currentDirectEntry
	 */
	private void updateResponse(DirectDDoSEntry currentDirectEntry) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 * @param currentDirectEntry
	 */
	private void addResponseList(DirectDDoSEntry currentDirectEntry) {
		buildResponseJob(currentDirectEntry);
		List<AttackFlow> attackFlowList = buildAttackFlow(currentDirectEntry);
		currentDirectEntry.setAttackFlowList(attackFlowList);
		ddoSAutomateService.commitL1DDoSAutomate(currentDirectEntry);
		ddosFlowService.commitL1AttackResponse(currentDirectEntry);			
	}
	/**
	 * 撤销响应规则
	 * @param directDDoSEntry
	 */
	private void deleteResponseList(DirectDDoSEntry directDDoSEntry) {
		ddoSAutomateService.cancelL1DDoSAutomate(directDDoSEntry);
		ddosFlowService.cancelL1AttackResponse(directDDoSEntry);
			
	}
	/**
	 * 新建与响应模型对于的响应作业JOB
	 * @param currentDirectEntry
	 */
	private void buildResponseJob(DirectDDoSEntry currentDirectEntry)
	{
		JobTable ja = new JobTable();
		AttackInfo attack= currentDirectEntry.getAttackInfo();
		long dstIP = attack.getIp();
		ja.setName("L1_DDoS响应_"+ HydraUtil.long2ip(dstIP));
		ja.setType(HydraConst.TABLE_RUN);
		ja.setAutomate(HydraConst.TABLE_AUTO);
		jobService.saveJobTable(ja);
		currentDirectEntry.setResponseJob(ja);
	}
	/**
	 * 生成对应的攻击流五元组
	 * @param attack
	 * @return
	 */
	private List<AttackFlow> buildAttackFlow(DirectDDoSEntry currentDirectEntry)
	{
		AttackInfo attack = currentDirectEntry.getAttackInfo();
		List<AttackFlow> attackFlowList = new ArrayList<AttackFlow>();
		List<OppositeInfo> oppositeList = currentDirectEntry.getOppositeList();
		if(oppositeList.size() > ruleThre)
		{
			   AttackFlow attackFlow = new AttackFlow();
			   attackFlow.setDstIP(attack.getIp());
			   attackFlow.setSrcIP(0);
			   attackFlowList.add(attackFlow);	
		}
		else
		{
		   for(OppositeInfo opposite : oppositeList)
		   {
			   AttackFlow attackFlow = new AttackFlow();
			   attackFlow.setDstIP(attack.getIp());
			   attackFlow.setSrcIP(opposite.getOpposite_ip());
			   attackFlowList.add(attackFlow);		
		   }
		}
		return attackFlowList;
	}
	/**
	 * 清理观察列表和响应列表
	 */
	public void refresh() {
		 refreshResponseList();
		 refreshWatchList();
	}
	/**
	 * 状态转换图中的5，目前采用了早期的方案，连续两次检测不到就移出
	 */
	private void refreshResponseList() {
		ArrayList<Long> tobeDelete = new ArrayList<Long>();
		Iterator<Entry<Long, DirectDDoSEntry>>  iterator = ddosResponse.entrySet().iterator();
		while(iterator.hasNext())
		{
			Entry<Long, DirectDDoSEntry> temEntry = iterator.next();
			Long ddos_id = temEntry.getKey();
			if(ddosResponseLast.containsKey(ddos_id))
			{
				int flag = ddosResponseLast.get(ddos_id);
				flag--;
				if(flag == 0)
					tobeDelete.add(ddos_id);
				else
					ddosResponseLast.put(ddos_id, flag);
			}
			else
			{
				tobeDelete.add(ddos_id);
			}
		}
		for(Long key : tobeDelete)
		{
			deleteResponseList(ddosResponse.get(key));
			ddosResponse.remove(key);
			ddosResponseLast.remove(key);
			ddosWatch.put(key, 1);  //退回观察列表
		}
		
	}
	/**
	 * 刷新观察列表
	 */
	private void refreshWatchList() {
		ArrayList<Long> tobeDelete = new ArrayList<Long>();
		Iterator<Entry<Long, Integer>>  iterator = ddosWatch.entrySet().iterator();
		while(iterator.hasNext())
		{
			Entry<Long, Integer> entry = iterator.next();
			Long temKey = entry.getKey();
			if(!ddosWatchLast.contains(temKey))
			{
				tobeDelete.add(temKey);
			}
		}
		for(Long loc : tobeDelete)
		{
			ddosWatch.remove(loc);
		}
	}
}
