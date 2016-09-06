package cn.edu.njnet.hydra.exenode.ovs;

import org.json.JSONObject;

import cn.edu.njnet.hydra.util.HydraUtil;

public class OFPMatch implements Cloneable
{
	private Integer  IN_PORT    = null;//in_port
	private Integer  ETH_TYPE   = 0x0800;//dl_type
	private Long     IPV4_SRC   = null;
	private Long     src_mask   = null;//ipv6_src=('2001:db8:bd05:1d2:288a:1fc0:1:10ee', 'ffff:ffff:ffff:ffff::'),
	private Long     IPV4_DST   = null;//ipv4_dst
	private Long     dst_mask   = null;
	private Integer  IP_PROTO   = null;//ip_proto
	private Integer  TCP_SRC    = null; //tcp_src
	private Integer  TCP_DST    = null;/* TCP destination port. */
	private Integer  UDP_SRC    = null;/* UDP source port. */
	private Integer  UDP_DST    = null;
    public OFPMatch(JSONObject json)
    {
    	
    }
    public OFPMatch()
    {

    }
    public OFPMatch cloneMatch()
    {
    	try {
			return (OFPMatch) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
	public JSONObject toJSONObject()
	{
		JSONObject jo = new JSONObject();
		jo.put("dl_type",ETH_TYPE);
		if(IN_PORT != null)
		{
			jo.put("in_port", IN_PORT);
		}
		if(IPV4_SRC != null && IPV4_SRC != 0)
		{
			if(src_mask == 0)
				src_mask = 0xffffffffL;
			jo.put("ipv4_src", HydraUtil.long2ip(IPV4_SRC)+"/"+HydraUtil.long2ip(src_mask));
		}
		if(IPV4_DST != null && IPV4_DST != 0)
		{
			if(dst_mask == 0)
				dst_mask = 0xffffffffL;
			jo.put("ipv4_dst", HydraUtil.long2ip(IPV4_DST)+"/"+HydraUtil.long2ip(dst_mask));
		}
		if (IP_PROTO != null) {
			jo.put("ip_proto", IP_PROTO);
			if (IP_PROTO.equals(new Integer(6)))// TCP
			{
				if (TCP_SRC >= 0) {
					jo.put("tcp_src", TCP_SRC);
				}
				if (TCP_DST >= 0) {
					jo.put("tcp_dst", TCP_DST);
				}
			} else if (IP_PROTO.equals(new Integer(17))) {
				if (UDP_SRC >= 0) {
					jo.put("udp_src", UDP_SRC);
				}
				if (UDP_DST >= 0) {
					jo.put("udp_dst", UDP_DST);
				}
			}
		}
		return jo;
	}
	public static void main(String[] args) 
	{
		OFPMatch match = new OFPMatch();
		match.setIPV4_DST(2886758400L);
		match.setDst_mask(4294967040L);
		System.out.println(match.toJSONObject().toString());
	}
	
	
	public Integer getIN_PORT() {
		return IN_PORT;
	}
	public void setIN_PORT(Integer iN_PORT) {
		IN_PORT = iN_PORT;
	}
	public Long getIPV4_SRC() {
		return IPV4_SRC;
	}
	public void setIPV4_SRC(Long iPV4_SRC) {
		IPV4_SRC = iPV4_SRC;
	}
	public Long getSrc_mask() {
		return src_mask;
	}
	public void setSrc_mask(Long src_mask) {
		this.src_mask = src_mask;
	}
	public Long getIPV4_DST() {
		return IPV4_DST;
	}
	public void setIPV4_DST(Long iPV4_DST) {
		IPV4_DST = iPV4_DST;
	}
	public Long getDst_mask() {
		return dst_mask;
	}
	public void setDst_mask(Long dst_mask) {
		this.dst_mask = dst_mask;
	}
	public Integer getIP_PROTO() {
		return IP_PROTO;
	}
	public void setIP_PROTO(Integer iP_PROTO) {
		IP_PROTO = iP_PROTO;
	}
	public Integer getTCP_SRC() {
		return TCP_SRC;
	}
	public void setTCP_SRC(Integer tCP_SRC) {
		TCP_SRC = tCP_SRC;
	}
	public Integer getTCP_DST() {
		return TCP_DST;
	}
	public void setTCP_DST(Integer tCP_DST) {
		TCP_DST = tCP_DST;
	}
	public Integer getUDP_SRC() {
		return UDP_SRC;
	}
	public void setUDP_SRC(Integer uDP_SRC) {
		UDP_SRC = uDP_SRC;
	}
	public Integer getUDP_DST() {
		return UDP_DST;
	}
	public void setUDP_DST(Integer uDP_DST) {
		UDP_DST = uDP_DST;
	}
}
