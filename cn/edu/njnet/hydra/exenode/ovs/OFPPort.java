package cn.edu.njnet.hydra.exenode.ovs;

public class OFPPort {
	private Integer id;
    private long  port_no;
    private OFPSwitch sw;
    private String port_name;
    private OFPPortStat lastStat;
    //private List<OFPPortStat> allstat;
    
    
    public OFPPort()
    {
    }
	public long getPort_no() {
		return port_no;
	}
	public void setPort_no(long port_no) {
		this.port_no = port_no;
		if(port_no == 4294967294L)//0xFFFFFFFE
		{
			port_name = "控制器";
		}
		else
		{
			port_name = Long.toString(port_no);
		}
	}
	public static void clear() {
		OFPPortStat.clear();
		
	}
	public OFPPortStat getLastStat() {
		return lastStat;
	}
	public void setLastStat(OFPPortStat lastStat) {
		this.lastStat = lastStat;
	}
	public String getPort_name() {
		return port_name;
	}
	public void setPort_name(String port_name) {
		this.port_name = port_name;
	}
	public OFPSwitch getSw() {
		return sw;
	}
	public void setSw(OFPSwitch sw) {
		this.sw = sw;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}


}
/*
 * public static List<OFPPort> getAllPort()
   {
		try 
		{
			Connection conn = DBconn.createConnection();
			conn.createStatement();
			Statement stmt = conn.createStatement();
			String sql = "select * from port_status group by ID;";
			//System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<OFPPort> allport = new ArrayList<OFPPort>();
			while (rs.next())
			{
				OFPPort ovsp = new OFPPort();
				OFPPortStat lps = new OFPPortStat();
				ovsp.setPort_no(rs.getLong("port_no"));
				//ovsp.setDatapath(rs.getLong("datapath"));
				ovsp.setLastStat(lps);
				lps.setRx_bytes(rs.getLong("rx_bytes"));
				lps.setRx_packets(rs.getLong("rx_packets"));
				lps.setRx_errors(rs.getLong("rx_errors"));
				lps.setTx_bytes(rs.getLong("tx_bytes"));
				lps.setTx_packets(rs.getLong("tx_packets"));
				lps.setTx_errors(rs.getLong("tx_errors"));
				lps.setCollisions(rs.getLong("collisions"));
				lps.setTs(rs.getTimestamp("time"));
				lps.setDuration_sec(rs.getLong("duration_sec"));
				allport.add(ovsp);
			}
			return allport;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}    	    	
    }
    */
