package somalilandPastoralistMovement.agents;

public class EthnicityClan {
	
	private int id;
	private String ethnicityName;
	private String clanName;
	
	
	
	public EthnicityClan(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public String getEthnicityName() {
		return ethnicityName;
	}
	public void setEthnicityName(String ethnicityName) {
		this.ethnicityName = ethnicityName;
	}
	public String getClanName() {
		return clanName;
	}
	public void setClanName(String clanName) {
		this.clanName = clanName;
	}
	
	
	

}
