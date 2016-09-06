package hydra.ddos.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

import hydra.ddos.pojo.AttackInfo;
import hydra.ddos.pojo.DirectDDoSEntry;
import hydra.ddos.pojo.IndirectDDoSEntry;

import javax.annotation.Resource;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.conf.NodeConfig;
import cn.edu.njnet.hydra.dbconn.DBconn;

public class DDoSAutomateService {
	@Resource
	private NodeConfig nodeConfig;
	
	public DDoSAutomateService()
	{
		
	}
	public void commitL1DDoSAutomate(DirectDDoSEntry directDDoSEntry)
	{
		try 
		{
			long jid = directDDoSEntry.getResponseJob().getID();
			AttackInfo attackInfo = directDDoSEntry.getAttackInfo();
			Connection conn = DBconn.getDDoSConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into l1_automate_response(ip,max_pps,max_kbps,start_time,valid, job_id)"
					+ "values(%d, %d, %d, UNIX_TIMESTAMP(now()),%d, %d)", attackInfo.getIp(), attackInfo.getMax_pps(),
					attackInfo.getMax_kbps(), HydraConst.DDOS_RESPONSE_VALID, jid);			
			String sql = fat.toString();
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet res = stmt.getGeneratedKeys();
			if(res.next())
			{
				directDDoSEntry.setL1ResponseID(res.getInt(1));
			}
			fat.close();
			res.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	public void cancelL1DDoSAutomate(DirectDDoSEntry directDDoSEntry)
	{
		try 
		{
			Connection conn = DBconn.getDDoSConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update l1_automate_response set valid=1,end_time=UNIX_TIMESTAMP(now()) where id=%d)", 
					directDDoSEntry.getL1ResponseID());			
			String sql = fat.toString();
			stmt.executeUpdate(sql);	
			fat.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	public void commitL2DDoSAutomate(IndirectDDoSEntry indirectDDoSEntry)
	{
		try 
		{
			long jid = indirectDDoSEntry.getResponseJob().getID();
			String loaction = indirectDDoSEntry.getAccessSchool();
			Integer maxKbps = indirectDDoSEntry.getMaxKBPS();
			Integer maxPPS  = indirectDDoSEntry.getMaxPPS();
			Connection conn = DBconn.getDDoSConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into l2_automate_response(location,max_pps,max_kbps,start_time, valid, job_id)"
					+ "values(%s, %d, %d, UNIX_TIMESTAMP(now()), %d)",loaction, maxPPS, maxKbps, 
					   HydraConst.DDOS_RESPONSE_VALID, jid);	
			String sql = fat.toString();
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet res = stmt.getGeneratedKeys();
			if(res.next())
			{
				indirectDDoSEntry.setL2ResponseID(res.getInt(1));
			}
			fat.close();
			res.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}			
	}
	public void cancelL2DDoSAutomate(IndirectDDoSEntry indirectDDoSEntry)
	{
		try 
		{
			Connection conn = DBconn.getDDoSConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("update l2_automate_response set end_time=UNIX_TIMESTAMP(now()) where id=%d)", 
					indirectDDoSEntry.getL2ResponseID());
			String sql = fat.toString();
			stmt.executeUpdate(sql);	
			fat.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}			
	}
	public void saveDDoSArg(String nameArg,String valueArg)
	{
		try 
		{
			Connection conn = DBconn.getDDoSConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("select * from node_stat where name='%s'", nameArg);
			String sql = fat.toString();
			ResultSet rs= stmt.executeQuery(sql);
			if(!rs.next())
			{
				rs.close();
				Formatter fat2 = new Formatter();
			    fat2.format("insert into node_stat(name,value) values('%s', '%s')", nameArg, valueArg);
			    sql = fat2.toString();
			    fat2.close();
			    stmt.executeUpdate(sql);	
			}
			else
			{
				rs.close();
				Formatter fat2 = new Formatter();
			    fat2.format("update node_stat set name='%s' where value='%s'", nameArg, valueArg);
			    sql = fat2.toString();
			    fat2.close();
			    stmt.executeUpdate(sql);				
			}
			fat.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
