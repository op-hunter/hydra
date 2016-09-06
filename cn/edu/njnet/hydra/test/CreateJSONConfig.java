package cn.edu.njnet.hydra.test;

import org.json.JSONArray;
import org.json.JSONObject;

public class CreateJSONConfig {
	public static JSONObject config1()
	{
		JSONObject js1 = new JSONObject();
		js1.put("dpid", "52243499860");
		js1.put("virtualip", "211.65.192.166");
		JSONObject js2 = new JSONObject();
		js2.put("dpid", "52235065406");
		js2.put("virtualip", "211.65.192.165");
		JSONArray jswitch = new JSONArray();
		jswitch.put(js1);
		jswitch.put(js2);
		JSONObject js = new JSONObject();
		JSONObject masterController = new JSONObject();
		masterController.put("restURL", "http://211.65.193.179:8080");
		masterController.put("ip","211.65.193.179");
		masterController.put("port",6633);
		masterController.put("name","������179");
		//
		
		JSONArray auxController = new JSONArray();
		JSONObject ac1 = new JSONObject();
		ac1.put("restURL", "http://211.65.193.183:8080");
		ac1.put("ip", "211.65.193.183");
		ac1.put("port", 6633);
	    ac1.put("name", "������183");
		auxController.put(ac1);
		
		js.put("auxController", auxController);
		js.put("masterController", masterController);
		js.put("baseSwitch", jswitch);
		return js;
	}
	public static JSONObject config2()
	{
		JSONObject js1 = new JSONObject();
		js1.put("dpid", "52243499860");
		js1.put("virtualip", "211.65.192.166");
		
		JSONArray jswitch = new JSONArray();
		jswitch.put(js1);

		JSONObject js = new JSONObject();
		JSONObject masterController = new JSONObject();
		masterController.put("restURL", "http://211.65.193.179:8080");
		masterController.put("ip","211.65.193.179");
		masterController.put("port",6633);
		masterController.put("name","������179");
		//
		
		JSONArray auxController = new JSONArray();
		JSONObject ac1 = new JSONObject();
		ac1.put("restURL", "http://211.65.193.183:8080");
		ac1.put("ip", "211.65.193.183");
		ac1.put("port", 6633);
	    ac1.put("name", "������183");
		auxController.put(ac1);
		
		js.put("auxController", auxController);
		js.put("masterController", masterController);
		js.put("baseSwitch", jswitch);
		return js;
	}

}
