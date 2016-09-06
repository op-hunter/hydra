package cn.edu.njnet.hydra.exenode.ovs;
/*
 * OFPCR_ROLE_NOCHANGE = 0     # Don't change current role.
OFPCR_ROLE_EQUAL = 1        # Default role, full access.
OFPCR_ROLE_MASTER = 2       # Full access, at most one master.
OFPCR_ROLE_SLAVE = 3 
 */
public enum ControllerRole {
	NOCHANGE(0),
	EQUAL(1),
	MASTER(2),
	SLAVE(3);
	
    private int value;

    private ControllerRole(int value) {
        this.value = value;
    }

    public static ControllerRole valueOfString(String value) {
        switch (value) 
        {
           case "MASTER":
               return MASTER;
           case "SLAVE":
               return SLAVE;
           case "NOCHANGE":
               return NOCHANGE;
           case "EQUAL":
               return EQUAL;
           default:
               return NOCHANGE;
        }
    }

    public String value() {
        switch (value) 
        {
           case 0:
               return "NOCHANGE";
           case 1:
               return "EQUAL";
           case 2:
               return "MASTER";
           case 3:
               return "SLAVE";
           default:
               return "NOCHANGE";
        }
    }
    public int getIntValue()
    {
    	return value;
    }
}
