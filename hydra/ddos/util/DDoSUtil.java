package hydra.ddos.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

import cn.edu.njnet.hydra.dbconn.DBconn;
import hydra.ddos.pojo.AttackInfo;
import hydra.ddos.pojo.OppositeInfo;

public class DDoSUtil {
	public static AttackInfo AttrsToAttackInfo(String[] attrs)
	{
		AttackInfo info = new AttackInfo();
		info.setDdos_id(Long.valueOf(attrs[0]));
		info.setIp(Long.valueOf(attrs[1]));
		info.setLocation(attrs[2]);
		info.setDdos_type(Integer.valueOf(attrs[3].substring(2),16));//DDOS_TYPE 16进制
		info.setAvg_pps(Long.valueOf(attrs[4]));
		info.setMax_pps(Long.valueOf(attrs[5]));
		info.setAvg_kbps(Long.valueOf(attrs[6]));
		info.setMax_kbps(Long.valueOf(attrs[7]));
		info.setStart_time(Long.valueOf(attrs[8]));
		info.setEnd_time(Long.valueOf(attrs[9]));
		return info;
	}
	public static OppositeInfo AttrsToOppositeInfo(String attrs[])
	{
		OppositeInfo info = new OppositeInfo();
		info.setOpposite_ip(Long.valueOf(attrs[0]));
		info.setOpposite_location(attrs[1]);
		info.setPkts_out(Long.valueOf(attrs[2]));
		info.setPkts_in(Long.valueOf(attrs[3])); 
		info.setBytes_out(Long.valueOf(attrs[4]));
		info.setPkts_in(Long.valueOf(attrs[5]));
		info.setPort(Integer.valueOf(attrs[6]));
		return info;
	}
}
