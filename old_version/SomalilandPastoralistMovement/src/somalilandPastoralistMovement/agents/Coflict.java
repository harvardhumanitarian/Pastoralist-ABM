package somalilandPastoralistMovement.agents;

import java.util.Date;

public class Coflict {
	
	private long eventid; //data_id field
	private Date eventDate; //event_date
	private int year;
	private String admin1;
	private String admin2;
	private double latX; //latitude;
	private double lonY; //longitude;
	
	public Coflict(long eventid) {
		super();
		this.eventid = eventid;
	}

	public long getEventid() {
		return eventid;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getAdmin1() {
		return admin1;
	}

	public void setAdmin1(String admin1) {
		this.admin1 = admin1;
	}

	public String getAdmin2() {
		return admin2;
	}

	public void setAdmin2(String admin2) {
		this.admin2 = admin2;
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
	
	
	
	

}
