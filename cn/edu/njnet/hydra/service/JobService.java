package cn.edu.njnet.hydra.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;
import javax.annotation.Resource;

import cn.edu.njnet.hydra.conf.HydraConst;
import cn.edu.njnet.hydra.conf.NodeConfig;
import cn.edu.njnet.hydra.dbconn.DBconn;
import cn.edu.njnet.hydra.exenode.ovs.JobTable;

public class JobService {
	@Resource
	private NodeConfig nodeConfig;
	
	public void saveJobTable(JobTable newJob)
	{
  		try {
 			Connection conn = DBconn.createConnection();
 			Statement stmt = conn.createStatement();
 			Formatter fat = new Formatter();
 			fat.format("insert into u_job_table(name, submit_time, automate, type, valid, u_id) "
 					+ " values('%s',UNIX_TIMESTAMP(now()), %d, %d, %d, %s);",newJob.getName(), newJob.getAutomate(),
 					    newJob.getType(), HydraConst.TABLE_VALID, nodeConfig.getNodeConfig("DDoS_USER_ID"));
 			String sql = fat.toString();
 			fat.close();
			fat.close();
			stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
            {
            	long id = (Long)rs.getObject(1);
            	newJob.setID(id);
            }
            else
            	newJob.setID(0);		
 			rs.close();
 			stmt.close();
 			
 		} catch (SQLException e) {
 			e.printStackTrace();
 		} 	
	}
	public void stopJob(JobTable newJob)
	{
  		try {
 			Connection conn = DBconn.createConnection();
 			Statement stmt = conn.createStatement();
 			Formatter fat = new Formatter();
 			fat.format("update u_job_table set type=%d where ID=%d",HydraConst.TABLE_STOP, newJob.getID());
 			String sql = fat.toString();
 			fat.close();
			fat.close();
			stmt.executeUpdate(sql);		
 			stmt.close();
 			newJob.setType(HydraConst.TABLE_STOP);
 		} catch (SQLException e) {
 			e.printStackTrace();
 		} 			
	}
}
