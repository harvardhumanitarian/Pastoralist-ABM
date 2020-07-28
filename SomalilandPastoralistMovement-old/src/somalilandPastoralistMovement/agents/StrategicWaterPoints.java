package somalilandPastoralistMovement.agents;

import java.util.Date;

public class StrategicWaterPoints {
	private int fid;
	private String name;
	private double latX;
	private double lonY;
	private String sourceType;
	private String admin1;
	private String distName;
	private Date recordDate;
	
	public StrategicWaterPoints(int fid) {
		this.fid= fid;
	}

	public int getFid() {
		return fid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatX() {
		return latX;
	}

	public void setLatX(double latX) {
		this.latX = latX;
	}

	public double getLonY() {
		return lonY;
	}

	public void setLonY(double lonY) {
		this.lonY = lonY;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getAdmin1() {
		return admin1;
	}

	public void setAdmin1(String admin1) {
		this.admin1 = admin1;
	}

	public String getDistName() {
		return distName;
	}

	public void setDistName(String distName) {
		this.distName = distName;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	
}
