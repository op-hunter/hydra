package hydra.ddos;

import hydra.ddos.main.AutomateDDoS;
import hydra.ddos.pojo.AttackInfo;
import hydra.ddos.pojo.DirectDDoSEntry;
import hydra.ddos.pojo.DirectDDoSList;
import hydra.ddos.pojo.OppositeInfo;
import hydra.ddos.util.DDoSUtil;

import java.io.FileOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;

import cn.edu.njnet.hydra.conf.ViewConf;
import cn.edu.njnet.hydra.dbconn.DBconn;
import cn.edu.njnet.hydra.util.HydraUtil;

public class HydraHandler implements Runnable {
	private Socket socket;	
	
	private FileOutputStream out;
	
	private ApplicationContext context;
	
	public HydraHandler()
	{
		
	}
	@Override
	/**
	 * 当前处理方法比较耗费内存，所有的socket内容会先读入文件，再做处理
	 */
	public void run() 
	{
		try {
			printlog("SOCKET START");
			List<String> listStr= IOUtils.readLines(this.socket.getInputStream(),"utf-8");	
			socket.close();
			printlog("SOCKET CLOSE");
			
			String filePath = HydraUtil.checkDDOSPath();
			long gran = getCurrentGran();
			long receiveTime = System.currentTimeMillis()/1000;
			String fileName = filePath + gran + ".ddos";
			out = new FileOutputStream(fileName);
			printlog("RECEIVE LINE:" + listStr.size());
			IOUtils.writeLines(listStr, "\n", out, "utf-8");
			out.close();
			printlog("FILE OUT FINISH");
			
			ddosHandler(listStr,gran, receiveTime);
			printlog("END");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 对NBOS发过来的DDOS攻击信息进行处理
	 * @param listStr 
	 */
	private void ddosHandler(List<String> listStr, long gran, long receiveTime)
	{
		int intGran = (int)(receiveTime);
		AutomateDDoS automateDDoS = (AutomateDDoS) context.getBean("automateDDoS");
		List<DirectDDoSEntry> currentDDoSList = new ArrayList<DirectDDoSEntry>();	
		int iter = 0;
		String buffer = null;
		while(iter < listStr.size())
		{		
			buffer = listStr.get(iter);
			iter++;
		    String[] attrs = buffer.split("[ ]+");
			if(attrs.length == 10)
			{
			    AttackInfo attack_info = DDoSUtil.AttrsToAttackInfo(attrs);
			    attack_info.setCurrentGran(intGran);			    
			    DirectDDoSEntry currentEntry = new DirectDDoSEntry();
			    currentEntry.setAttackInfo(attack_info);
			    List<OppositeInfo> oppositeList = new ArrayList<OppositeInfo>();
			    while(iter < listStr.size())
			    {
			    	buffer = listStr.get(iter);
			    	iter++;
			    	attrs = buffer.split("[ ]+");
				    if(attrs.length == 7)
					{
					    OppositeInfo opposite_info = DDoSUtil.AttrsToOppositeInfo(attrs);
					    opposite_info.setDdos_id(attack_info.getDdos_id());
					    opposite_info.setCurrentGran(intGran);
					    oppositeList.add(opposite_info);
					}
				    else
				    {
				    	currentEntry.setOppositeList(oppositeList);
				    	currentEntry.getAttackInfo().setOppositeNum(oppositeList.size());
				    	currentDDoSList.add(currentEntry);
				    	break ;
				    }
			    }			    	
			}
			else
			{
				if(iter < listStr.size())
				   buffer = listStr.get(iter);
			}
		}
		DirectDDoSList directDDoSEntry = new DirectDDoSList();
		directDDoSEntry.setDDoSEntries(currentDDoSList);
		automateDDoS.addDirectDDos(directDDoSEntry);
		saveOriginLog(currentDDoSList);
	}
	/**
	 * 保存收到的原始日志，以备查询
	 * @param currentDDoSList
	 */
	private void saveOriginLog(List<DirectDDoSEntry> currentDDoSList) {
		for(DirectDDoSEntry entry : currentDDoSList)
		{
			AttackInfo attack = entry.getAttackInfo();
			saveAttackInfo(attack);		
		}
		
	}
	/**
	 * 目前并不在数据库中保存对端的数量
	 * 
	 * @param info
	 */
	private void saveOppositeInfo(OppositeInfo info) {
		try 
		{
			Connection conn = DBconn.getLogConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into nbos_opposite_info(opposite_ip, "
					+ "location, pkts_out, pkts_in, bytes_out, "
					+ "bytes_in, port, ddos_id, gran) values(%d, "
					+ "'%s', %d, %d, %d,"
					+ "%d, %d, %d, %d)",info.getOpposite_ip(),
					  info.getOpposite_location(), info.getPkts_out(), 
					  info.getPkts_in(), info.getBytes_out(),
					  info.getPkts_in(), info.getPort(), info.getDdos_id(), info.getCurrentGran());
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	private void saveAttackInfo(AttackInfo info) {
		try 
		{
			Connection conn = DBconn.getLogConnection();
			Statement stmt = conn.createStatement();
			Formatter fat = new Formatter();
			fat.format("insert into nbos_attack_info(ddos_id, "
					+ "ip, location, ddos_type, avg_pps, "
					+ "max_pps, avg_kbps, max_kbps, start_time,"
					+ "end_time, granularity_num, gran, opposite_num) values(%d, "
					+ "%d, '%s', %d, %d,"
					+ "%d, %d, %d, %d,"
					+ "%d, %d, %d, %d)",info.getDdos_id(),
					info.getIp(), info.getLocation(), info.getDdos_type(), info.getAvg_pps(),
					info.getMax_pps(), info.getAvg_kbps(), info.getMax_kbps(), info.getStart_time(),
					info.getEnd_time(), info.getGranularity_num(),info.getCurrentGran(),info.getOppositeNum());
			String sql = fat.toString();
			fat.close();
			stmt.executeUpdate(sql);	
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
	}
	/**
	 * 给同一次收到的所有DDoS攻击打上相同的时间戳
	 */
	public long getCurrentGran()
	{
		long gran = (int) ((System.currentTimeMillis()/1000)/300) * 300;
		return gran;
	}
	private void printlog(String infoStr)
	{
		ViewConf viewConf = ViewConf.getViewConf();
		SimpleDateFormat dateFormat = viewConf.getDateFormat();
		Date date = new Date();
		String timeStr = dateFormat.format(date).toString();	
		System.out.printf("THREAD %s TIME:%s\n", infoStr,timeStr);

	}
	/**
	 * 目前的文件名有些问题，应该用NBOS的时间粒度来表示
	 */
	private String getFileName(long gran)
	{
		Date date = new Date(gran);
		ViewConf viewConf = ViewConf.getViewConf();
		SimpleDateFormat dateFormat = viewConf.getFileformat();
		String fileName = dateFormat.format(date).toString();
		return fileName;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public ApplicationContext getContext() {
		return context;
	}
	public void setContext(ApplicationContext context) {
		this.context = context;
	}
	
}
