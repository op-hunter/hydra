package hydra.ddos.pojo;

public class AttackFlow {
	private long  srcIP;          //攻击源IP
	private long  dstIP;          //被攻击IP
	private String srcLocation;  //对端IP归属
	private String dstLocation;
	private int   avgKBps;          //bps平均强度
	private int   avgPPS;
	private int   srcPort;  
	private int   dstPort;
    private long  ddosID;        //ddos_id
	public long getSrcIP() {
		return srcIP;
	}
	public void setSrcIP(long srcIP) {
		this.srcIP = srcIP;
	}
	public long getDstIP() {
		return dstIP;
	}
	public void setDstIP(long dstIP) {
		this.dstIP = dstIP;
	}
	public String getSrcLocation() {
		return srcLocation;
	}
	public void setSrcLocation(String srcLocation) {
		this.srcLocation = srcLocation;
	}
	public String getDstLocation() {
		return dstLocation;
	}
	public void setDstLocation(String dstLocation) {
		this.dstLocation = dstLocation;
	}
	public int getAvgKBps() {
		return avgKBps;
	}
	public void setAvgKBps(int avgKBps) {
		this.avgKBps = avgKBps;
	}
	public int getAvgPPS() {
		return avgPPS;
	}
	public void setAvgPPS(int avgPPS) {
		this.avgPPS = avgPPS;
	}
	public long getDdosID() {
		return ddosID;
	}
	public void setDdosID(long ddosID) {
		this.ddosID = ddosID;
	}
	public int getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}
	public int getDstPort() {
		return dstPort;
	}
	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}
	
}
