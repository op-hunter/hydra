package hydra.ddos.pojo;

import java.util.ArrayList;
import java.util.List;

import cn.edu.njnet.hydra.exenode.ovs.JobTable;

public class IndirectDDoSEntry {
	private String accessSchool;
	private List<AttackFlow> attackFlowList;
	private Integer l2ResponseID;
	private List<AttackFlow> responseFlowList;
	private int maxPPS;
	private int maxKBPS;
	private JobTable responseJob;
	
	public String getAccessSchool() {
		return accessSchool;
	}
	public void setAccessSchool(String accessSchool) {
		this.accessSchool = accessSchool;
	}
	public List<AttackFlow> getAttackFlowList() {
		return attackFlowList;
	}
	public void setAttackFlowList(List<AttackFlow> attackFlows) {
		this.attackFlowList = attackFlows;
	}
	public int getMaxPPS() {
		return maxPPS;
	}
	public void setMaxPPS(int avgPPS) {
		this.maxPPS = avgPPS;
	}
	public int getMaxKBPS() {
		return maxKBPS;
	}
	public void setMaxKBPS(int avgKBPS) {
		this.maxKBPS = avgKBPS;
	}
	
	public JobTable getResponseJob() {
		return responseJob;
	}
	public void setResponseJob(JobTable responseJob) {
		this.responseJob = responseJob;
	}
	
	public List<AttackFlow> getResponseFlowList() {
		return responseFlowList;
	}
	public void setResponseFlowList(List<AttackFlow> responseFlowList) {
		this.responseFlowList = responseFlowList;
	}
	
	public Integer getL2ResponseID() {
		return l2ResponseID;
	}
	public void setL2ResponseID(Integer l2ResponseID) {
		this.l2ResponseID = l2ResponseID;
	}
	public void addAttackFLow(AttackFlow attackFlow)
	{
		if(attackFlowList == null)
			attackFlowList = new ArrayList<AttackFlow>();
		attackFlowList.add(attackFlow);
	}
	public void calcIntensity()
	{
		maxPPS = 0;
		maxKBPS = 0;
		for(AttackFlow attack : attackFlowList)
		{
			maxPPS += attack.getAvgPPS();
			maxKBPS += attack.getAvgKBps();
		}
	}
}
