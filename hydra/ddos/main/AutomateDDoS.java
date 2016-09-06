package hydra.ddos.main;

import hydra.ddos.model.DirectHandle;
import hydra.ddos.model.IndirectHandle;
import hydra.ddos.pojo.AttackFlow;
import hydra.ddos.pojo.AttackInfo;
import hydra.ddos.pojo.DirectDDoSEntry;
import hydra.ddos.pojo.DirectDDoSList;
import hydra.ddos.pojo.IndirectDDoSEntry;
import hydra.ddos.pojo.IndirectDDoSList;
import hydra.ddos.pojo.OppositeInfo;
import hydra.ddos.service.DDoSAutomateService;
import hydra.ddos.service.IPLocationService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import cn.edu.njnet.hydra.conf.NodeConfig;
import cn.edu.njnet.hydra.conf.ViewConf;

public class AutomateDDoS  implements Runnable {
    
	private LinkedBlockingQueue<DirectDDoSList> directDDoSList;
	
	@Resource
	private DirectHandle directHandle;
	@Resource
	private IndirectHandle indirectHandle;
	@Resource
	private IPLocationService ipLocationService;
	@Resource
	private DDoSAutomateService ddoSAutomateService;
	@Resource
	private NodeConfig nodeConfig;
	
	private PrintStream writer;
	
	public AutomateDDoS()
	{
		directDDoSList = new LinkedBlockingQueue<DirectDDoSList>();
	}
	@Override
	public void run() {
		updateArg();
		while(true)
		{
			try {
				DirectDDoSList currentDirectList = directDDoSList.take();
				if (currentDirectList != null) {
					IndirectDDoSList currentIndirectList = mergeDDoS(currentDirectList);
					printLog(currentIndirectList);
					indirectHandle.preventDDoS(currentIndirectList);
					directHandle.preventDDoS(currentDirectList);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void updateArg() {
		ddoSAutomateService.saveDDoSArg("L2_MAX_KPPS", nodeConfig.getNodeConfig("L2_MAX_KPPS"));
		ddoSAutomateService.saveDDoSArg("L2_MAX_MBPS", nodeConfig.getNodeConfig("L2_MAX_MPPS"));
		ddoSAutomateService.saveDDoSArg("L1_MAX_KPPS", nodeConfig.getNodeConfig("L1_MAX_KPPS"));
		ddoSAutomateService.saveDDoSArg("L1_MAX_MBPS", nodeConfig.getNodeConfig("L1_MAX_MPPS"));
		ddoSAutomateService.saveDDoSArg("DDOS_WATCH_THRESHOLD", nodeConfig.getNodeConfig("DDOS_WATCH_THRESHOLD"));
		ddoSAutomateService.saveDDoSArg("DDOS_RESPONSE_THRESHOLD", nodeConfig.getNodeConfig("DDOS_RESPONSE_THRESHOLD"));
		ddoSAutomateService.saveDDoSArg("WATCH_TIME_THRESHOLD", nodeConfig.getNodeConfig("WATCH_TIME_THRESHOLD"));
	}
	/**
	 * 短期解决办法，用来完成对响应模块的开发
	 * @param currentIndirectList
	 */
	private void printLog(IndirectDDoSList currentIndirectList) {
		try {
			writer = new PrintStream(new FileOutputStream("ddos_log",true),true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ViewConf viewConf = ViewConf.getViewConf();
		SimpleDateFormat dateFormat = viewConf.getDateFormat();
		Date date = new Date();
		String timeStr = dateFormat.format(date).toString();	
		int size = currentIndirectList.getIndirectDDoSEntries().size();
		writer.println("AutoMateDDOS:" + timeStr +" IndirectEntry Size:"+ size);
		if(size != 0)
		{
			List<IndirectDDoSEntry> temList = currentIndirectList.getIndirectDDoSEntries();
			for(IndirectDDoSEntry entry : temList)
			{
				writer.printf("  %s %d %d %d\n",entry.getAccessSchool(), entry.getAttackFlowList().size(), 
						     entry.getMaxKBPS(),entry.getMaxPPS());
			}
		}
		
	}
	/**
	 * 
	 * @param currentDirectList
	 * @return
	 */
	private IndirectDDoSList mergeDDoS(DirectDDoSList currentDirectList) {
		IndirectDDoSList indirectDDoSList = new IndirectDDoSList();
		HashMap<String, IndirectDDoSEntry> locationMap = new HashMap<String, IndirectDDoSEntry>();
		List<DirectDDoSEntry> ddosEntry = currentDirectList.getDDoSEntries();
		for(DirectDDoSEntry entry : ddosEntry)
		{
			AttackInfo attack = entry.getAttackInfo();
			long dstIP = attack.getIp();
			String location = ipLocationService.getLocation(dstIP);
			if(location == null)
			{
				continue ;
			}
			IndirectDDoSEntry indirectDDoSEntry;
			if(locationMap.containsKey(location))
			{
				indirectDDoSEntry = locationMap.get(location);
			}
			else
			{
				indirectDDoSEntry = new IndirectDDoSEntry();
				indirectDDoSEntry.setAccessSchool(location);
			}
			List<OppositeInfo> oppositeInfoList = entry.getOppositeList();
			long attackTime = attack.getEnd_time() - attack.getStart_time();
			if(attackTime == 0)
			{
				continue;
			}
			for(OppositeInfo opposite : oppositeInfoList)
			{
			   AttackFlow attackFlow = new AttackFlow();
			   attackFlow.setDstIP(dstIP);
			   attackFlow.setSrcIP(opposite.getOpposite_ip());
			   int avgkbps = (int) (opposite.getBytes_out() / attackTime / 1000);
			   int avgpps = (int) (opposite.getPkts_out() / attackTime);
			   attackFlow.setAvgKBps(avgkbps);
			   attackFlow.setAvgPPS(avgpps);
			   attackFlow.setSrcPort(opposite.getPort());
			   indirectDDoSEntry.addAttackFLow(attackFlow);
			}

		}
		List<IndirectDDoSEntry> indirectDDoSEntries = new ArrayList<IndirectDDoSEntry>();
		Iterator<Entry<String, IndirectDDoSEntry>> iterator = locationMap.entrySet().iterator();
		while(iterator.hasNext())
		{
			Entry<String, IndirectDDoSEntry> temEntry= iterator.next();
			IndirectDDoSEntry temInDDosEntry = temEntry.getValue();
			temInDDosEntry.calcIntensity();
			indirectDDoSEntries.add(temInDDosEntry);
		}
		indirectDDoSList.setIndirectDDoSEntries(indirectDDoSEntries);
		return indirectDDoSList;
	}
	/**
	 * 将直接攻击项加入队列
	 * 同时完成间接攻击项的归并
	 * @param directDDoSEntry
	 */
	public void addDirectDDos(DirectDDoSList directDDoSEntry)
	{
		if(directDDoSEntry == null)
			return ;
		try {
			directDDoSList.put(directDDoSEntry);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
