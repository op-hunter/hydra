package hydra.ddos.pojo;

class TrieNode {
	boolean isAddress;
	
	HydraIPAddress hydraIPAddress;
	
	TrieNode[] arr;
    public TrieNode() 
    {
    	isAddress = false;
        arr = new TrieNode[2];
    }
}

public class IPTrie {
    private TrieNode root;
    
    private long IPMASKArray[];

    public IPTrie() 
    {
        root = new TrieNode();
        IPMASKArray = new long[33];
        long k = 1;
        for(int i = 32; i >= 0;i--)
        {
        	IPMASKArray[i] = k;
        	k <<= 1;
        }
    }

    public void insert(HydraIPAddress hydraIPAddress)
    {
    	TrieNode prev = root;
    	TrieNode curr = null;
    	int i = 0;
    	int index = 0;
    	int IPMask = hydraIPAddress.getIPMask();
    	long IPAddress = hydraIPAddress.getIPAddress();
    	while(i <= IPMask)
    	{
    		if( (IPAddress & IPMASKArray[i]) > 0)
    			index = 1;
    		else
    			index = 0;
    		curr = prev.arr[index];
    		if(curr == null)
    		{
    			curr = new TrieNode();
    			prev.arr[index] = curr;
    		}
    		prev = curr;
    		i++;
    	}
    	prev.isAddress = true;
    	prev.hydraIPAddress = hydraIPAddress;
    	
    }
    /**
     * 查询指定的IP的归属
     * @param hydraIPAddress
     * @return
     */
    public boolean getLocation(HydraIPAddress hydraIPAddress) 
    {
    	TrieNode curr = root;
    	int i = 0;
    	int index = 0;
    	int IPMask = hydraIPAddress.getIPMask();
    	long IPAddress = hydraIPAddress.getIPAddress();
    	while(i <= IPMask)
    	{
    		if( (IPAddress & IPMASKArray[i]) > 0)
    			index = 1;
    		else
    			index = 0;
    		curr = curr.arr[index];
    		if(curr.isAddress)
    		{
    			hydraIPAddress.setLocation(curr.hydraIPAddress.getLocation());
    			return true;
    		}
    		i++;
    	}
    	return false;        
    }
    /**
     * 
     * @param ipAddress
     * @return
     */
    public String getLocation(long ipAddress)
    {
    	TrieNode curr = root;
    	int i = 0;
    	int index = 0;
    	int IPMask = 32;
    	while(i <= IPMask)
    	{
    		if( (ipAddress & IPMASKArray[i]) > 0)
    			index = 1;
    		else
    			index = 0;
    		curr = curr.arr[index];
    		if(curr == null)
    			return null;
    		if(curr.isAddress)
    		{
    			return curr.hydraIPAddress.getLocation();
    		}
    		i++;
    	}
    	return null;     	
    }
}
