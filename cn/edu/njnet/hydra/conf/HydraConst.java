package cn.edu.njnet.hydra.conf;

public final class HydraConst {	
	public final static int FLOW_STD = 0;
	public final static int FLOW_ADD = 1;
	public final static int FLOW_MOD = 2;
	public final static int FLOW_DEL = 3;

	public final static int TABLE_RUN = 0;
	public final static int TABLE_STOP = 1;
	
	public final static int TABLE_VALID = 0;
	public final static int TABLE_INVALID = 1;

	public final static int TABLE_AUTO = 0;
	public final static int TABLE_MANUAL = 1;
	
	public final static int DDOS_RESPONSE_VALID = 0;
	public final static int DDOS_RESPONSE_INVALID = 1;
	
	public final static int FLOW_ACTION_DROP    = 0;
	public final static int FLOW_ACTION_FORWARD = 1;
	public final static int FLOW_ACTION_CAPTURE = 2;

	public final static int FLOW_VALID   = 1;
	public final static int FLOW_INVALID = 0;
	
	public final static int PATTERN_AND = 1;
	public final static int PATTERN_OR  = 2;
	public final static int PATTERN_INVALID = 0;
	
	public final static int STD_PRIORITY = 300;
	
	public final static int OVS_STD     = 0;	
	public final static int OVS_DISCONNECT = 3;
	
	public final static int U_ACTION_DROP    = 0;
	public final static int U_ACTION_FORWARD = 1;
	public final static int U_ACTION_CAPTURE = 2;
	
	public final static int UDP = 17;
	public final static int TCP = 6;
	
	public final static int CONTROLLER_STD = 0;
	public final static int CONTROLLER_DISCONNECT = 1;
	public final static int CONTROLLER_ERROR = 2;
    
	public final static int URLERROR = 0;
	public final static int URLCONNECTIONERROR = 1;
}
