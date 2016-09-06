package cn.edu.njnet.hydra.zookeeper;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.json.JSONObject;


public class ConfigChangeWatch implements Watcher {
	private List<ZooCallBack> callList = new ArrayList<ZooCallBack>();
	private String waPath;
	private ZooApi zoo;
	private Stat st;
	
	public ConfigChangeWatch(String path, ZooApi sample)
	{
		waPath = path;
		zoo = sample;
		st = new Stat();
	}
	@Override
	public void process(WatchedEvent event) {
		System.out.println("WATCHER: " + event.toString());		
		Watcher.Event.EventType type    = event.getType();
		if(type.equals(Watcher.Event.EventType.NodeDeleted))
		{
			System.err.println("Zookeeper Node be deleted!");
			System.exit(0);
		}
		else if(type.equals(Watcher.Event.EventType.NodeDataChanged))
		{
			String str = zoo.readData(waPath,this,st);
			JSONObject jo = null;
			if(str == null)
				jo = new JSONObject(str);
			else 
				jo = new JSONObject();
			CallChange(jo);
		}

	}
	private void CallChange(JSONObject js)
	{
		for(ZooCallBack call : callList)
		{
			call.CallBack();
		}
	}
	public void addCallBack(ZooCallBack zcb)
	{
		callList.add(zcb);
	}
}
