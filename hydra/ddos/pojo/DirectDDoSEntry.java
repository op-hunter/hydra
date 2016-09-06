package hydra.ddos.pojo;

import java.util.List;

import cn.edu.njnet.hydra.exenode.ovs.JobTable;

public class DirectDDoSEntry {
	private Integer l1ResponseID;
	private AttackInfo attackInfo;
	private List<OppositeInfo> oppositeList;
	private List<AttackFlow> attackFlowList;
	private int maxPPS;
	private int maxKBPS;
	private JobTable responseJob;
	
	public AttackInfo getAttackInfo() {
		return attackInfo;
	}
	public void setAttackInfo(AttackInfo attackInfo) {
		this.attackInfo = attackInfo;
	}
	public List<OppositeInfo> getOppositeList() {
		return oppositeList;
	}
	public void setOppositeList(List<OppositeInfo> oppositeList) {
		this.oppositeList = oppositeList;
	}
	public int getMaxPPS() {
		return maxPPS;
	}
	public void setMaxPPS(int maxPPS) {
		this.maxPPS = maxPPS;
	}
	public int getMaxKBPS() {
		return maxKBPS;
	}
	public void setMaxKBPS(int maxKBPS) {
		this.maxKBPS = maxKBPS;
	}
	public JobTable getResponseJob() {
		return responseJob;
	}
	public void setResponseJob(JobTable responseJob) {
		this.responseJob = responseJob;
	}
	public List<AttackFlow> getAttackFlowList() {
		return attackFlowList;
	}
	public void setAttackFlowList(List<AttackFlow> attackFlowList) {
		this.attackFlowList = attackFlowList;
	}
	public Integer getL1ResponseID() {
		return l1ResponseID;
	}
	public void setL1ResponseID(Integer l1ResponseID) {
		this.l1ResponseID = l1ResponseID;
	}
}
