package hydra.ddos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;

import hydra.log.HydraLogger;
import cn.edu.njnet.hydra.conf.NodeConfig;


public class HydraServer implements Runnable{
    
	private static final int port = 8833;

	private ApplicationContext context;
	
	private HydraLogger hydralogger = null;
	
	public HydraServer()
	{
		hydralogger = new HydraLogger(NodeConfig.getNodeConfig().getNodeConfig("JOB_LOG"),"job_log");
	}
	public void run() {
		ThreadPoolExecutor socketPool = new ThreadPoolExecutor(1,3,3,TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(100));
		int listen_port = HydraServer.port;
		ServerSocket server = null;
		try{
			server = new ServerSocket(listen_port);
			System.out.println("HYDRA-NBOS Interface listening:"+ listen_port);
			hydralogger.Write("Interface listening:" + Integer.toString(listen_port));
			Socket socket = null;
			while(true)
			{
				socket = server.accept();
				HydraHandler hydraHandler = new HydraHandler();
				hydraHandler.setSocket(socket);
				System.out.println("NEW SOCKET");
				hydraHandler.setContext(context);
				socketPool.execute(hydraHandler);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public ApplicationContext getContext() {
		return context;
	}
	public void setContext(ApplicationContext context) {
		this.context = context;
	}
}
