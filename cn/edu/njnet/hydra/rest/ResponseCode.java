package cn.edu.njnet.hydra.rest;

public class ResponseCode {
    private int value;

    public ResponseCode()
    {
    	
    }
    public ResponseCode(int val)
    {
    	value = val;
    }
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
    
}
