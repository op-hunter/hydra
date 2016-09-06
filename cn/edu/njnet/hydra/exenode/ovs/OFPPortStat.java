package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Formatter;

import org.json.JSONObject;

import cn.edu.njnet.hydra.dbconn.DBconn;

public class OFPPortStat {
	private Long id;
	private long rx_packets;
	private long rx_bytes;
	private long rx_errors;

	private long tx_packets;
	private long tx_bytes;
	private long tx_errors;

	private long collisions;
	private long duration_sec;
	private Timestamp ts;
	
    private OFPPort port;
        
    public OFPPortStat(OFPPort por)
    {
    	port = por;
    }
	public void updateStat(JSONObject jo,long swid) 
	{
		 updateData(jo);
         if(id == null)
         {
        	 save();
         }
         else
         {
        	 update();
         }
	}
    private void updateData(JSONObject jo) 
    {
    	rx_packets   = jo.getLong("rx_packets");
    	rx_bytes     = jo.getLong("rx_bytes");
    	rx_errors    = jo.getLong("rx_errors");
    	
    	tx_packets   = jo.getLong("tx_packets");
    	tx_bytes     = jo.getLong("tx_bytes");
    	tx_errors    = jo.getLong("tx_errors");
    	
    	collisions   = jo.getLong("collisions");
    	duration_sec = jo.getLong("duration_sec");
		
	}
	private void save()
    {
		if(check())
		{
			_save();
		}
		else
		{
			update();
		}

    }
    private boolean check() {
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format(
					"select id from port_status where port_no=%d and dpid=%d;",
					port.getPort_no(), port.getSw().getDpid());
			String sql = fat.toString();
			
			fat.close();
			stmt.executeQuery(sql);
			
			ResultSet rs = stmt.getResultSet();
			if (rs.next()) {
				this.id = (Long) rs.getObject(1);

				rs.close();
				stmt.close();
				return false;
			} else {

				rs.close();
				stmt.close();
				this.id = null;
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}
    private void _save()
    {
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into port_status "
					+ " (port_no,rx_packets,rx_bytes,rx_errors,tx_packets,tx_bytes,"
					+ " tx_errors,collisions,duration_sec,time,dpid,switch_id) "
					+ " values( %d, %d, %d, %d, %d, %d, %d, %d, %d, now(), %d, %d);",
					port.getPort_no(),rx_packets,rx_bytes,rx_errors,tx_packets,tx_bytes,
					tx_errors,collisions,duration_sec,port.getSw().getDpid(),port.getSw().getId());
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
                this.id = (Long)rs.getObject(1);
            else
            	this.id = null;
			//System.out.println(sql);			
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
	private void update()
    {
		try 
		{
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update port_status "
					+ " set port_no=%d, rx_packets=%d, rx_bytes=%d, rx_errors=%d, tx_packets=%d, tx_bytes=%d,"
					+ " tx_errors=%d, collisions=%d, duration_sec=%d, time=now()  "
					+ " where id =%d;",
					port.getPort_no(),rx_packets,rx_bytes,rx_errors,tx_packets,tx_bytes,
					tx_errors,collisions,duration_sec,this.id);
			String sql = fat.toString();
			fat.close();			
			stmt.executeUpdate(sql);
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      	
    }
	public long getRx_packets() {
		return rx_packets;
	}

	public void setRx_packets(long rx_packets) {
		this.rx_packets = rx_packets;
	}

	public long getRx_bytes() {
		return rx_bytes;
	}

	public void setRx_bytes(long rx_bytes) {
		this.rx_bytes = rx_bytes;
	}

	public long getRx_errors() {
		return rx_errors;
	}

	public void setRx_errors(long rx_errors) {
		this.rx_errors = rx_errors;
	}

	public long getTx_packets() {
		return tx_packets;
	}

	public void setTx_packets(long tx_packets) {
		this.tx_packets = tx_packets;
	}

	public long getTx_bytes() {
		return tx_bytes;
	}

	public void setTx_bytes(long tx_bytes) {
		this.tx_bytes = tx_bytes;
	}

	public long getTx_errors() {
		return tx_errors;
	}

	public void setTx_errors(long tx_errors) {
		this.tx_errors = tx_errors;
	}

	public long getCollisions() {
		return collisions;
	}

	public void setCollisions(long collisions) {
		this.collisions = collisions;
	}

	public long getDuration_sec() {
		return duration_sec;
	}

	public void setDuration_sec(long duration_sec) {
		this.duration_sec = duration_sec;
	}

	public Timestamp getTs() {
		return ts;
	}

	public void setTs(Timestamp ts) {
		this.ts = ts;
	}

	public OFPPort getPort() {
		return port;
	}

	public void setPort(OFPPort port) {
		this.port = port;
	}

	public static void clear() {
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			String sql = "delete from port_status;";
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
