package hydra.ddos.pojo;

public class AttackInfo {
	private long ddos_id ;          //攻击编号
	private long ip;                //被攻击IP
	private String location;        //IP归属
	private int ddos_type;          //攻击类型
	private long avg_pps;           //平均强度（pps）
	private long max_pps;           //最大强度（pps）
	private long avg_kbps;          //平均强度（KBps）
	private long max_kbps;          //最大强度（KBps）
	private long start_time;        //攻击开始时间
	private long end_time;          //攻击结束时间
	private int granularity_num;    //活跃时间粒度数
    private int currentGran;
    private long receiveTime;       //接收时间
    private int  oppositeNum;
	public AttackInfo()
	{
		
	}

	public long getDdos_id() {
		return ddos_id;
	}

	public void setDdos_id(long ddos_id) {
		this.ddos_id = ddos_id;
	}

	public long getIp() {
		return ip;
	}

	public void setIp(long ip) {
		this.ip = ip;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getDdos_type() {
		return ddos_type;
	}

	public void setDdos_type(int ddos_type) {
		this.ddos_type = ddos_type;
	}

	public long getAvg_pps() {
		return avg_pps;
	}

	public void setAvg_pps(long avg_pps) {
		this.avg_pps = avg_pps;
	}

	public long getMax_pps() {
		return max_pps;
	}

	public void setMax_pps(long max_pps) {
		this.max_pps = max_pps;
	}

	public long getAvg_kbps() {
		return avg_kbps;
	}

	public void setAvg_kbps(long avg_kbps) {
		this.avg_kbps = avg_kbps;
	}

	public long getMax_kbps() {
		return max_kbps;
	}

	public void setMax_kbps(long max_kbps) {
		this.max_kbps = max_kbps;
	}

	public long getStart_time() {
		return start_time;
	}

	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	public long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	public int getGranularity_num() {
		return granularity_num;
	}

	public void setGranularity_num(int granularity_num) {
		this.granularity_num = granularity_num;
	}

	public int getCurrentGran() {
		return currentGran;
	}

	public void setCurrentGran(int currentGran) {
		this.currentGran = currentGran;
	}

	public long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public int getOppositeNum() {
		return oppositeNum;
	}

	public void setOppositeNum(int oppositeNum) {
		this.oppositeNum = oppositeNum;
	}
	
}
