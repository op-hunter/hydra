package cn.edu.njnet.hydra.util;

import java.io.File;

import cn.edu.njnet.hydra.conf.NodeConfig;

public class HydraUtil {
	/*
	 * 将long型IPv4地址转化为点分十进制的String型
	 */
    public static String long2ip(long longip)
    {
    
    	long s1 = longip%256;
    	longip /= 256;
    	long s2 = longip%256;
    	longip /= 256;
    	long s3 = longip%256;
    	longip /= 256;
    	long s4 = longip%256;
    	String ips = s4 + "." + s3 + "." + s2 + "." + s1;
    	return ips;
    }
    /*
     * 将点分十进制的IPv4地址转化为long型
     */
    public static Long ip2long(String str)
    {
    	String[] arr = str.split("\\.");
    	long tem = 0;
    	for(int i=0;i<4;i++)
    	{
    	   tem <<= 8;
    	   tem += Integer.valueOf(arr[i]);   	   
    	}
    	return tem;
    }
    public static String getClassPath()
    {
    	String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
    	path        = path.substring(5);
    	/*
    	 * 关于classpath 还需要整理一下，目前的解决方案并不理想
    	 */
    	path        += "../classes/";
    	return path;
    }
	public static boolean checkPathDirectory(String pathStr)
	{
		File filePath = new File(pathStr);
		if(!filePath.exists())//目录不存在，则创建目录
			filePath.mkdirs();
		else if(!filePath.isDirectory())//存在，但不是目录
			return false;
		if(filePath.isDirectory())
			return true;
		else
			return false;
	}
	public static String checkDDOSPath()
	{
		String path = NodeConfig.getNodeConfig().getNodeConfig("NBOS_DDOS_FILE_PATH");
		boolean pathFlag = HydraUtil.checkPathDirectory(path);
		if(!pathFlag)
		{
			System.err.printf("path %s not a directory");
			System.exit(0);
		}
        return path;
	}
	public static void main(String[] args) 
	{
		System.out.println(HydraUtil.getClassPath());
		ClassLoader cl = HydraUtil.class.getClassLoader();
		String path = cl.getResource("/hydra.conf").getPath().toString();
		System.out.print(path);

	}

}
