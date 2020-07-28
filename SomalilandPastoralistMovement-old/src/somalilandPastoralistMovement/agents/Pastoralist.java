package somalilandPastoralistMovement.agents;

import java.util.ArrayList;
import java.util.List;

public class Pastoralist {
	
	private long pastoralistId;
	
	private String originAdmin1Level;
	private String originAdmin2Level;
	private String originClan;
	private String originEthnicity;
	
	private boolean isPastoralist;
	private int changeFromPastoralistTick;
	
	private String dropoutSeason;
	
	private String currentAdmin1Level;
	private String currentAdmin2Level;
	private String currentClan;
	private String currentEthnicity;
	private String currentStatus;
	private int seasonalStrikes;
	
	private List<String> latLongPerTick;
	
	public Pastoralist(long pid) {
		
		this.pastoralistId = pid;
		this.originAdmin1Level = "";
		this.originAdmin2Level = "";
		this.originClan = "";
		this.originEthnicity = "";
		this.currentAdmin1Level = "";
		this.currentAdmin2Level = "";
		this.currentClan = "";
		this.currentEthnicity = "";
		this.currentStatus = "";
		
		this.isPastoralist = true;
		this.changeFromPastoralistTick = -1;
		this.seasonalStrikes = 0;
		this.dropoutSeason = "";
		
		this.latLongPerTick = new ArrayList<String>();
		
	}


	public String getOriginAdmin1Level() {
		return originAdmin1Level;
	}

	public void setOriginAdmin1Level(String originAdmin1Level) {
		this.originAdmin1Level = originAdmin1Level;
	}

	public String getOriginAdmin2Level() {
		return originAdmin2Level;
	}

	public void setOriginAdmin2Level(String originAdmin2Level) {
		this.originAdmin2Level = originAdmin2Level;
	}

	public String getOriginClan() {
		return originClan;
	}

	public void setOriginClan(String originClan) {
		this.originClan = originClan;
	}

	public String getOriginEthnicity() {
		return originEthnicity;
	}

	public void setOriginEthnicity(String originEthnicity) {
		this.originEthnicity = originEthnicity;
	}

	public String getCurrentAdmin1Level() {
		return currentAdmin1Level;
	}

	public void setCurrentAdmin1Level(String currentAdmin1Level) {
		this.currentAdmin1Level = currentAdmin1Level;
	}

	public String getCurrentAdmin2Level() {
		return currentAdmin2Level;
	}

	public void setCurrentAdmin2Level(String currentAdmin2Level) {
		this.currentAdmin2Level = currentAdmin2Level;
	}

	public String getCurrentClan() {
		return currentClan;
	}

	public void setCurrentClan(String currentClan) {
		this.currentClan = currentClan;
	}

	public String getCurrentEthnicity() {
		return currentEthnicity;
	}

	public void setCurrentEthnicity(String currentEthnicity) {
		this.currentEthnicity = currentEthnicity;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}


	public long getPastoralistId() {
		return pastoralistId;
	}

	@Override
	public String toString() {
		String sep = ",";
		StringBuilder sb = new StringBuilder();
		sb.append(this.pastoralistId);
		sb.append(sep);
		sb.append(this.originClan);
		sb.append(sep);
		sb.append(this.originEthnicity);
		sb.append(sep);
		sb.append(this.originAdmin2Level);
		sb.append(sep);
		sb.append(this.originAdmin1Level);
		
		return sb.toString();
	}

	public boolean isThisPastoralist() {
		return isPastoralist;
	}

	public void setIsPastoralist(boolean isNotPastoralist) {
		this.isPastoralist = isNotPastoralist;
	}

	public int getChangeFromPastoralistTick() {
		return changeFromPastoralistTick;
	}

	public void setChangeFromPastoralistTick(int changeFromPastoralistTick) {
		this.changeFromPastoralistTick = changeFromPastoralistTick;
	}

	public List<String> getLatLongPerTick() {
		return latLongPerTick;
	}

	public void setLatLongPerTick(List<String> latLongPerTick) {
		this.latLongPerTick = latLongPerTick;
	}


	public int getSeasonalStrikes() {
		return seasonalStrikes;
	}


	public void setSeasonalStrikes(int seasonalStrikes) {
		this.seasonalStrikes = seasonalStrikes;
	}


	public String getDropoutSeason() {
		return dropoutSeason;
	}


	public void setDropoutSeason(String dropoutSeason) {
		this.dropoutSeason = dropoutSeason;
	}

}
