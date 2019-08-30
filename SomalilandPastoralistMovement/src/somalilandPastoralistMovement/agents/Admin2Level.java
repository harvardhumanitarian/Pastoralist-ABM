package somalilandPastoralistMovement.agents;

public class Admin2Level {
	
	private int admin2Id;
	private String admin2Name;
	private String admin1Name;
	
	public Admin2Level(int admin2Id) {
		this.admin2Id = admin2Id;
	}
	public int getAdmin2Id() {
		return admin2Id;
	}
	public String getAdmin2Name() {
		return admin2Name;
	}
	public void setAdmin2Name(String admin2Name) {
		this.admin2Name = admin2Name;
	}
	public String getAdmin1Name() {
		return admin1Name;
	}
	public void setAdmin1Name(String admin1Name) {
		this.admin1Name = admin1Name;
	}

	
}
