package hydra.ddos.pojo;

public class DDoSFlow {
	private long srcIP;
	private long dstIP;
	private int proto;
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
	public int getProto() {
		return proto;
	}
	public void setProto(int proto) {
		this.proto = proto;
	}
	

}
