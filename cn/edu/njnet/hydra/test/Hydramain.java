package cn.edu.njnet.hydra.test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;





//import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import cn.edu.njnet.hydra.exenode.ovs.*;

public class Hydramain {

	public void getSwitch()
	{
		 System.out.println("Starting Statistics JAXB client.");
		 
		 String baseURL = "http://211.65.193.179:8080/one/nb/v2/statistics";
		 String containerName = "default";
		 String user = "admin";
		 String password = "admin";
		 
		 URL url;
		 try 
		 {
		 url = new java.net.URL("http://211.65.192.171:8080/stats/desc/13136560299");
		 System.out.println(url.toString());
		 
		 URLConnection connection = url.openConnection();
		 connection.setRequestProperty("Content-Type", "application/xml");
		 connection.setRequestProperty("Accept", "application/json");		 
		 connection.connect();
		 InputStream input = connection.getInputStream();
		 BufferedInputStream bi = new BufferedInputStream(input);
		 
		 byte[] buffer = new byte[4096];
		 int length = bi.read(buffer);
		 String str = new String(buffer);
		 System.out.println(str);

         JSONObject js = new JSONObject(str);
       
         System.out.println(js.getJSONObject("13136560299").getString("mfr_desc"));
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }		
	}
	public static void SetFlow()
	{	 
		 URL url;
		 try 
		 {
		 url = new java.net.URL("http://211.65.197.150:8080/stats/flowentry/add");
		 System.out.println(url.toString());
		 
		 HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		 connection.setRequestProperty("Content-Type", "application/xml");
		 connection.setRequestProperty("Accept", "application/json");
		 connection.setRequestMethod("POST");
		 //connection.setc
		 //table_id
		 String str = "{'dpid':409152722251404,\"table_id\":0,'match':{'dl_type':0x0800,'ipv4_src':'211.65.193.0/255.255.255.0'},'priority':13333,'actions':{}";
		 byte[] bytes = str.toString().getBytes();
		 connection.setDoOutput(true);
		 connection.getOutputStream().write(bytes);
		 connection.connect();
		 InputStream input = connection.getInputStream();
		 BufferedInputStream bi = new BufferedInputStream(input);
		 
		 byte[] buffer = new byte[4096];
		 int length = bi.read(buffer);
		 String str2 = new String(buffer);
		 System.out.println(str2);

        //JSONObject js = new JSONObject(str2);
      
        //System.out.println(js.getJSONObject("13136560299").getString("mfr_desc"));
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }		
	}
	public void postFlow()
	{
		
	}
	public static void main(String[] args) {
		
		SetFlow();

	}

}
