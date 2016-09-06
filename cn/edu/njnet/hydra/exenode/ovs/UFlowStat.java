package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

import cn.edu.njnet.hydra.dbconn.DBconn;

public class UFlowStat {
    private Long ID;
    private Integer duraction_sec;
    private Long packet_count;
    private Long byte_count;
    private UFlow uf;
    
    
    public UFlowStat()
    {
    	duraction_sec = 0;
    	packet_count  = 0L;
    	byte_count    = 0L;    	
    }
    public void save()
    {
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into u_flow_status(duraction_sec,packet_count,byte_count,update_time,u_f_id) "
					+ " values(%d, %d, %d,now(),%d);",
					duraction_sec,packet_count,byte_count,uf.getID());
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    public void update()
    {
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update u_flow_status set duraction_sec=%d,packet_count=%d,byte_count=%d,update_time=now() where id =%d;",
					duraction_sec,packet_count,byte_count,ID);
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public Integer getDuraction_sec() {
		return duraction_sec;
	}

	public void setDuraction_sec(Integer duraction_sec) {
		this.duraction_sec = duraction_sec;
	}

	public Long getPacket_count() {
		return packet_count;
	}

	public void setPacket_count(Long packet_count) {
		this.packet_count = packet_count;
	}

	public Long getByte_count() {
		return byte_count;
	}

	public void setByte_count(Long byte_count) {
		this.byte_count = byte_count;
	}

	public UFlow getUf() {
		return uf;
	}

	public void setUf(UFlow uf) {
		this.uf = uf;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
