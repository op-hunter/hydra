package hydra.ddos.pojo;

public class OppositeInfo {
	private long opposite_ip;          //对端IP
	private String opposite_location;  //对端IP归属
	private long  pkts_out;            //对端发送的报文数
	private long  pkts_in;             //对端接收的报文数
	private long  bytes_out;           //对端发送的字节数
	private long  bytes_in;            //对端接收的字节数
	private int   port;                //端口号
    private long  ddos_id;              //ddos_id
    private int currentGran;
    private long receiveTime;
	public OppositeInfo()
	{
		
	}

	public long getOpposite_ip() {
		return opposite_ip;
	}

	public void setOpposite_ip(long opposite_ip) {
		this.opposite_ip = opposite_ip;
	}

	public String getOpposite_location() {
		return opposite_location;
	}

	public void setOpposite_location(String opposite_location) {
		this.opposite_location = opposite_location;
	}

	public long getPkts_out() {
		return pkts_out;
	}

	public void setPkts_out(long pkts_out) {
		this.pkts_out = pkts_out;
	}

	public long getPkts_in() {
		return pkts_in;
	}

	public void setPkts_in(long pkts_in) {
		this.pkts_in = pkts_in;
	}

	public long getBytes_out() {
		return bytes_out;
	}

	public void setBytes_out(long bytes_out) {
		this.bytes_out = bytes_out;
	}

	public long getBytes_in() {
		return bytes_in;
	}

	public void setBytes_in(long bytes_in) {
		this.bytes_in = bytes_in;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getDdos_id() {
		return ddos_id;
	}

	public void setDdos_id(long ddos_id) {
		this.ddos_id = ddos_id;
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
	
}
