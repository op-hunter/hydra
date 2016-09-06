package hydra.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.edu.njnet.hydra.conf.NodeConfig;

public class HydraLogger {

	private String logpath = null;
	private String logname = null;
	
	public HydraLogger(String path){
		logpath = path;
		logname = "Undefined";
		this.init();
	}
	
	public HydraLogger(String path,String name){
		logpath = path;
		logname = name;
		this.init();
	}
	
	public void init(){
		File f = new File(logpath);
		if(!f.exists())
			f.mkdirs();
	}
	
	/**
	 * @param message
	 */
	public void Write(String message){
		try{
			String file = logpath + File.separator + logname + ".log";
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(message + System.getProperty("line.separator"));
			bw.close();
			fw.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void setPath(String path){
		logpath = path;
	}
	public String getPath(){
		return logpath;
	}
	public void setName(String name){
		logname = name;
	}
	public String getName(){
		return logname;
	}
	
	public static void main(String[] args){
		String file = NodeConfig.getNodeConfig().getNodeConfig("JOB_LOG");
		HydraLogger hl = new HydraLogger(file);
		hl.Write("Hello" + System.getProperty("line.separator") + "World!");
		System.out.println("bye");
	}
}
