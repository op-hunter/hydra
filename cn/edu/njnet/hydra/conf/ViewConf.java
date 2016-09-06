package cn.edu.njnet.hydra.conf;

import java.text.SimpleDateFormat;

public class ViewConf {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private ViewConf()
	{
		
	}
	public static ViewConf getViewConf()
	{
		ViewConf vc = new ViewConf();
		return vc;
	}
	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}
	public SimpleDateFormat getFileformat() {
		return fileFormat;
	}
}
