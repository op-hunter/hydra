package cn.edu.njnet.hydra.rest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import org.apache.commons.io.IOUtils;

import cn.edu.njnet.hydra.conf.HydraConst;

public class HydraRestRequestUtil {
	
    public static int maxResponseContent = 1024*1024;
    public static int defaultBuffer = 4 * 1024;
    
	public static String httpConnection(String method, String host, String url,
			String args,ResponseCode code) {
		String cururl = host + url;
		URL curl;
		HttpURLConnection connection;
		try {
			curl = new java.net.URL(cururl);
			connection = (HttpURLConnection) curl.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestMethod(method);

			if (method.equals("POST")) {
				byte[] bytes = args.toString().getBytes();
				connection.setDoOutput(true);
				connection.getOutputStream().write(bytes);
			}
		} catch (Exception e) {
			code.setValue(HydraConst.URLERROR);
			return null;
		}
		try {
			connection.connect();
			InputStream input = connection.getInputStream();
			String str = IOUtils.toString(input);
			code.setValue(connection.getResponseCode());
			return str;
		} catch (Exception e) {
			System.out.println(method + " " + cururl + " ERROR");
			try {
				code.setValue(connection.getResponseCode());
			} catch (IOException e1) {
				code.setValue(HydraConst.URLCONNECTIONERROR);
			}
			return null;
		}

	}
}
