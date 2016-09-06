package cn.edu.njnet.hydra.exenode.ovs;

import java.sql.Timestamp;

public class JobTable {
    private long ID;
    private String name;
    private Timestamp Submittime;
    private int type;
    private int automate;
    
    public JobTable()
    {
    	
    }
    public long getID() {
		return ID;
	}
	public void setID(long iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getSubmittime() {
		return Submittime;
	}
	public void setSubmittime(Timestamp submittime) {
		Submittime = submittime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAutomate() {
		return automate;
	}
	public void setAutomate(int automate) {
		this.automate = automate;
	}
}
