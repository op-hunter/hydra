package cn.edu.njnet.hydra.exenode.ovs;

public enum UAction {
	DROP(0),
	FORWORD(1),
	CAPTURE(2);

    private int value = 0;

    private UAction(int value) {    //    ������private�ģ�����������
        this.value = value;
    }

    public static UAction valueOf(int value) {    //    ��д�Ĵ�int��enum��ת������
        switch (value) 
        {
        case 0:
            return DROP;
        case 1:
            return FORWORD;
        case 2:
            return CAPTURE;
        default:
            return null;
        }
    }

    public int value() {
        return this.value;
    }
}
/*
import org.json.JSONObject;

public class UAction {
    private HAction type;
    private Integer port;
    private UFlow uf;
    public UAction()
    {
    	
    }
    public UAction(JSONObject js)
    {
    	
    }
    
	public HAction getType() {
		return type;
	}
	public void setType(HAction type) {
		this.type = type;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public UFlow getUf() {
		return uf;
	}
	public void setUf(UFlow uf) {
		this.uf = uf;
	}
	public JSONObject toJSONObject() 
	{
		if(type == HAction.DROP)
		    return null;
		else if(type == HAction.OUTPUT)
		{
			JSONObject jo = new JSONObject();
			jo.put("type","OUTPUT");
			jo.put("port",port);
			return jo;
		}
		return null;
	}
}
*/