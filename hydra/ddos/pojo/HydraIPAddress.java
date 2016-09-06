package hydra.ddos.pojo;

public class HydraIPAddress {
	private long IPAddress;
	private int IPMask;
	private String location;
	private String locationID;
	
	public HydraIPAddress()
	{
		
	}
	public long getIPAddress() {
		return IPAddress;
	}
	public void setIPAddress(long iPAddress) {
		IPAddress = iPAddress;
	}
	public int getIPMask() {
		return IPMask;
	}
	public void setIPMask(int iPMask) {
		IPMask = iPMask;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocationID() {
		return locationID;
	}
	public void setLocationID(String locationID) {
		this.locationID = locationID;
	}
	
}
