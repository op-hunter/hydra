package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

import cn.edu.njnet.hydra.dbconn.DBconn;

public class OFPFlowStat {
	private Long ID;
	private OFPFlow hflow;
	private Long duration_sec;
	private Long packet_count;
	private Long byte_count;

	public OFPFlowStat()
	{
		duration_sec = 0L;
		packet_count = 0L;
		byte_count   = 0L;
	}
	/*
	 * 保存流状态
	 */
	public void save()
	{
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into h_flow_status(duration_sec,packet_count,byte_count,h_f_id) "
					+ " values(%d, %d, %d,%d);",
					duration_sec,packet_count,byte_count,hflow.getID());
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
                this.ID = (Long)rs.getObject(1);
            else
            	this.ID = null;
			System.out.println(sql);			
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * δ�������ݿ����ʧ�ܣ���������
	 */
	public void update()
	{
		try {
			Connection conn = DBconn.createConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update h_flow_status set duration_sec=%d,packet_count=%d,byte_count=%d wherer h_f_id + %d) "
					+ " values(%d, %d, %d,%d);",
					duration_sec,packet_count,byte_count,hflow.getID());
			String sql = fat.toString();
			fat.close();
			//int a = 
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
	public OFPFlow getHflow() {
		return hflow;
	}
	public void setHflow(OFPFlow hflow) {
		this.hflow = hflow;
	}
	public Long getDuration_sec() {
		return duration_sec;
	}
	public void setDuration_sec(Long duration_sec) {
		this.duration_sec = duration_sec;
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
}
