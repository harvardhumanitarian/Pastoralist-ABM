package somalilandPastoralistMovement.agents;

public class PrivateLandSettlemets {
	
	private int id;
	private double areaSqKm;
	
	
	public PrivateLandSettlemets(int id, double area) {
		this.id = id;
		this.areaSqKm = area;
	}


	public int getId() {
		return id;
	}


	public double getAreaSqKm() {
		return areaSqKm;
	}

	
}
