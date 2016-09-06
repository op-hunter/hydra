package cn.edu.njnet.hydra.main;

import hydra.ddos.HydraServer;
import hydra.ddos.main.AutomateDDoS;
import hydra.log.HydraLogger;
import cn.edu.njnet.hydra.conf.NodeConfig;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HydraExe {
	
	private OFPMonitor ofpMonitor;
	private HydraServer hydraServer;
	private AutomateDDoS automateDDoS;

	
    private Thread ofmThread;	
    private Thread recThread;
    private Thread ddosThread;
    
	private ApplicationContext context;
	
	private HydraLogger hydralogger = null;
	
	public HydraExe(){
		
	}

	public void MonitorStart()
	{
		System.out.println("ENTER MAIN");
		hydralogger.Write("ENTER MAIN");
		ofpMonitor.init();// 系统主线程
		ofmThread = new Thread(ofpMonitor);
		ofmThread.start();
		System.out.println("ofmThread start!");
		hydralogger.Write("ofmThread start!");
		recThread = new Thread(hydraServer);//ddos 攻击信息接收线程
		recThread.start();
		System.out.println("recThread start!");
		hydralogger.Write("recThread start!");
		ddosThread = new Thread(automateDDoS);//ddos 响应线程
		ddosThread.start();
		System.out.println("ddosThread start!");
		hydralogger.Write("ddosThread start!");
	}
	private void init() {
		context = new ClassPathXmlApplicationContext(new String[] {"hydra.xml"});
		ofpMonitor = (OFPMonitor) context.getBean("ofpMonitor");
		hydraServer = (HydraServer) context.getBean("hydraServer");
		automateDDoS = (AutomateDDoS) context.getBean("automateDDoS");
		hydraServer.setContext(context);
		hydralogger = new HydraLogger(NodeConfig.getNodeConfig().getNodeConfig("JOB_LOG"),"job_log");
	}
	public static void main(String[] args){
		HydraExe he = new HydraExe();
		he.init();
		he.MonitorStart();
	}


}
