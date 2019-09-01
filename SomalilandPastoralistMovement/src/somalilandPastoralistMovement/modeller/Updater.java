package somalilandPastoralistMovement.modeller;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.query.space.gis.WithinQuery;
import repast.simphony.space.gis.Geography;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;
import somalilandPastoralistMovement.agents.Pastoralist;
import somalilandPastoralistMovement.agents.PrivateLandSettlemets;

public class Updater {
	
	Utils utils = new Utils();
	Context <Object > context;
	int currentTick;
	Geography somalilandGeo;
	boolean seasonChangeFlag = false;
	

	@ScheduledMethod(start=1, interval=1, priority=ScheduleParameters.FIRST_PRIORITY )
	public void step() {
		currentTick = (int) RepastEssentials.GetTickCount();
		SomalilandContextCreator.currentDate = SomalilandContextCreator.currentDate.plusMonths(1);
		int m = SomalilandContextCreator.currentDate.getMonthValue();
		String season = SomalilandContextCreator.monthVsSeason.get(Month.of(m));
		
		somalilandGeo = SomalilandContextCreator.SomalilandGeographyObject;
		//SomalilandGeographyObject = (Geography) context.getProjection("SomalilandGeography");
		
		seasonChangeFlag = false;
		if(!SomalilandContextCreator.currentSeason.equals(season)) {
			somalilandGeo.removeCoverage(ResourceConstants.SEASONAL_CONFLICT_LAYER_MINUS_1);
			GridCoverage2D conflict_cvg = somalilandGeo.getCoverage(ResourceConstants.SEASONAL_CONFLICT_LAYER);
			somalilandGeo.addCoverage(ResourceConstants.SEASONAL_CONFLICT_LAYER_MINUS_1, conflict_cvg);
			somalilandGeo.removeCoverage(ResourceConstants.MODIS_LAYER);
			somalilandGeo.removeCoverage(ResourceConstants.SEASONAL_CONFLICT_LAYER);
			SomalilandContextCreator.currentSeason = season;
			System.out.println("Updating Vegetation Layer.");
			updateSeasonalLayers();
			seasonChangeFlag = true;
		}
		
		System.out.println(" ---------------------- Tick Number : " + currentTick + " :: Season change flag : " + seasonChangeFlag);
		
		// task for every agent at every tick
		movePastoralistAgent();
		
		if(currentTick == SomalilandContextCreator.totalTicks) {
			IndexedIterable<Object> pIterator= context.getObjects(Pastoralist.class);   
	        List<Pastoralist> pastoralists = new ArrayList<Pastoralist>();
	        for (Object o: pIterator){
	        	Pastoralist p = (Pastoralist) o;
	        	pastoralists.add(p);
	        }
			utils.writeOutput(pastoralists, SomalilandContextCreator.iteration);
			System.out.println("End of simulation.");
			RunEnvironment.getInstance().endRun();
		}
		
	}
	
	
	Integer getRandomInRange(int length, int minInclusive, int maxExclusive) {
		Random r = new Random();
		return r.ints(minInclusive, (maxExclusive)).limit(1).findFirst().getAsInt();
		//r.ints(length, minInclusive, maxExclusive).;
	}
	
	
	private Double getRandomInRange(int length, double minInclusive, double maxExclusive) {
		Random r = new Random();
		return r.doubles(minInclusive, (maxExclusive)).limit(1).findFirst().getAsDouble();
		//r.ints(length, minInclusive, maxExclusive).;
	}

	
	/**
	 * This method is designed to incorporate all functions required for moving the pastoralist agent. 
	 * Chose the best location to move depending upon the method for calculating the score of favorability of the next location,
	 * depending upon factors like vegetation and water availability etc.
	 * We consider seasonality effect, vegetation availability, man-made water points and conflicts so far.
	 * TODO: We need to add further movement restriction rules, depletion rates etc.
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void movePastoralistAgent() {
		// get all pastoralist agents
		context = ContextUtils.getContext(this);
		IndexedIterable<Object> pIterator= context.getObjects(Pastoralist.class);   
        List<Pastoralist> pastoralists = new ArrayList<Pastoralist>();
        for (Object o: pIterator){
        	Pastoralist p = (Pastoralist) o;
        	pastoralists.add(p);
        }
        
        int k=1;
        for(Pastoralist p : pastoralists) {
        	System.out.println("PID: " + p.getPastoralistId() + " :: status : " + p.isThisPastoralist());
        	if(p.isThisPastoralist()) {
        		// calculate favorability score of locations in the scouting/grazing approx. monthly range, 
        		// and choose the location with highest score
        		Integer instantScoutingRange = getRandomInRange(1, SomalilandContextCreator.minScoutingRange, SomalilandContextCreator.maxScoutingRange+1); 
        		String bestLatxLatyScore = getBestFavorabilityScoreLocation(instantScoutingRange, p);
        		Geometry g = somalilandGeo.getGeometry(p);
        		Coordinate coord = g.getCoordinate();
        		List<String> latLongPerTick = p.getLatLongPerTick();
        		if(bestLatxLatyScore.equals(ResourceConstants.PASTORALIST_TO_IDP)) {
        			p.setIsPastoralist(false);
        			p.setChangeFromPastoralistTick(currentTick);
        			latLongPerTick.add(currentTick, ResourceConstants.PASTORALIST_TO_IDP);
        		} else {
        			// move agent to the next location
        			coord.x = Double.parseDouble(bestLatxLatyScore.split(",")[0]);
        			coord.y = Double.parseDouble(bestLatxLatyScore.split(",")[1]);
        			latLongPerTick.add(currentTick, bestLatxLatyScore+","+instantScoutingRange);
        			p.setLatLongPerTick(latLongPerTick);
        			somalilandGeo.move(p, g); 
        		}
        		/*
        		if(k==50) //TODO how many agents to process
        			break;
        		k++;
        		*/
        	}
        }
	}

	
	/**
	 * Additive model with normalized/stadardized scores of vegetation, conflict, etc layers
	 * TODO: check if crosses ethnic boundary, public/private land
	 * @param instantScoutingRange 
	 * @param p 
	 * @return
	 */
	String getBestFavorabilityScoreLocation(Integer instantScoutingRange, Pastoralist thisPastoralist) {
		System.out.println("Scouting range: " + instantScoutingRange);
		// get current location of the agent
    	String currentLatxLongy = thisPastoralist.getLatLongPerTick().get(currentTick-1);
    	System.out.println("Current lat long : " + currentLatxLongy);
    	/*Geometry g = SomalilandGeographyObject.getGeometry(p);
    	Coordinate coord = g.getCoordinate();
    	currentLatxLongy = coord.x + "," + coord.y;*/
		double currentLatX = Double.parseDouble(currentLatxLongy.split(",")[0]);
		double currentLonY = Double.parseDouble(currentLatxLongy.split(",")[1]);
		int pixelResolution = 1; // 1km is the raster pixel resolution
		// Candidate locations are obtained by creating a nth order moore OR radial neighborhood at the scouting range
		// get the lat,long of candidate locations.
		List<String> candidateLocations = mooreNeighborhood(currentLatX, currentLonY, instantScoutingRange);
		
		// get the SCORE by the additive model for each of these locations
		Map<Double, String> scoreVsLocation = getAdditiveModelScore(candidateLocations);
		
		
		List<Double> scoreKeyArray = new ArrayList<Double>(scoreVsLocation.keySet());
		Collections.sort(scoreKeyArray, Collections.reverseOrder()); //highest to lowest score
		double bestScore = scoreKeyArray.get(0);
		String tempLocation = scoreVsLocation.get(bestScore);
		String bestCellLatLonScore = tempLocation + "," + bestScore; // chose the highest score cell
		System.out.println("v1 : Candidate best score : " + bestCellLatLonScore);
		
		if(seasonChangeFlag) {
			String immediatePreviousLoc = currentLatxLongy;
			immediatePreviousLoc = immediatePreviousLoc.split(",")[0] + "," + immediatePreviousLoc.split(",")[1];
			System.out.println("Season Change: Temp Location = " + tempLocation + " :: ImmediatePreviouisLoc = " + immediatePreviousLoc);
			if(tempLocation.equals(immediatePreviousLoc)) { //check if last location and current location are same inspite of season change
				System.out.println("Season change and match location Finding next best location.");
				scoreVsLocation.remove(scoreKeyArray.get(0));
				scoreKeyArray = new ArrayList<Double>(scoreVsLocation.keySet());
				Collections.sort(scoreKeyArray, Collections.reverseOrder()); //highest to lowest score
				if(scoreVsLocation.size()>= 3) {
					int index = getRandomInRange(1, 0, 3);
					bestScore = scoreKeyArray.get(index);
					tempLocation = scoreVsLocation.get(bestScore);
				} else if(scoreVsLocation.size() == 2) {
					int index = getRandomInRange(1, 0, 2);
					bestScore = scoreKeyArray.get(index);
					tempLocation = scoreVsLocation.get(bestScore);
				} else if(scoreVsLocation.size() == 1) {
					//System.out.println(" ******************** Not enough locations: "  + scoreVsLocation.size());
					bestScore = scoreKeyArray.get(0);
					tempLocation = scoreVsLocation.get(bestScore);
				} 
			} 
			
			bestCellLatLonScore = tempLocation;
			bestCellLatLonScore = bestCellLatLonScore + "," + bestScore;
			System.out.println("v2 : Candidate best score : " + bestCellLatLonScore);
		} 
		
		int strike = 0;
		bestCellLatLonScore = privateSettlementDeal(bestCellLatLonScore, scoreVsLocation, strike);
		System.out.println("v3 : FINAL Candidate best score after private deal: " + bestCellLatLonScore);
		
		return bestCellLatLonScore;
	}


	private String privateSettlementDeal(String bestCellLatLonScore, Map<Double, String> scoreVsLocation, int strike) {
		System.out.println("privateSettlementDeal()");
		String bestCell = bestCellLatLonScore;
		List<Double> scoreKeyArray = new ArrayList<Double>(scoreVsLocation.keySet());
		Collections.sort(scoreKeyArray, Collections.reverseOrder()); //highest to lowest score
		
		if(scoreVsLocation.size() == 1) {
			bestCell = bestCellLatLonScore;
			System.out.println("scoreVsLocation.size = 1");
		}
		else {
			GeometryFactory fac = new GeometryFactory();
			Geometry geom = fac.createPoint(new Coordinate(Double.parseDouble(bestCellLatLonScore.split(",")[0]), Double.parseDouble(bestCellLatLonScore.split(",")[1])));
			@SuppressWarnings({ "rawtypes", "unchecked" })
			WithinQuery geoQuery1 = new WithinQuery(somalilandGeo, geom);
			for (Object obj : geoQuery1.query()) {
				int dealCode = 2; //public land default
				if (obj instanceof PrivateLandSettlemets) {
					//PrivateLandSettlemets pls = (PrivateLandSettlemets) obj;
					//System.out.println(firstNextLoc + " :: this best point is in private settlements area :: " + pls.getAreaSqKm());
					// wet season private land deal-making
					if(SomalilandContextCreator.currentSeason.equals(ResourceConstants.GU_SPRING) || 
							SomalilandContextCreator.currentSeason.equals(ResourceConstants.DEYR_AUTUMN)) {
						dealCode = getPrivateLandDealCode(strike, 0.5);
					} else { // dry season private land deal-making
						dealCode = getPrivateLandDealCode(strike, 0.25);
					}
					System.out.println("Private deal code : " + dealCode);
					if(dealCode == 1) { // deal
						bestCell = bestCellLatLonScore;
					} else if (dealCode == 0) { // dropout
						bestCell = ResourceConstants.PASTORALIST_TO_IDP;
					} else if(dealCode == -1) { // no deal
						strike++;
						// find new location 
						System.out.println("Strike" + strike + " :: new location search initiated..");
						System.out.println("No. of locs  " + scoreVsLocation.size());
						double s = Double.parseDouble(bestCellLatLonScore.split(",")[2]);
						scoreVsLocation.remove(s);
						scoreKeyArray = new ArrayList<Double>(scoreVsLocation.keySet());
						Collections.sort(scoreKeyArray, Collections.reverseOrder()); //highest to lowest score
						bestCellLatLonScore = scoreVsLocation.get(scoreKeyArray.get(0)) + "," + scoreKeyArray.get(0);
						System.out.println("New location =  " + bestCellLatLonScore + " :: size = " + scoreVsLocation.size());
						bestCell = privateSettlementDeal(bestCellLatLonScore, scoreVsLocation, strike);
					} else {
						System.out.println("**** ERROR: Invalid dealCode." + dealCode);
					}
					
					
				} /*else {
			System.out.println(firstLoc + " :: NOT in private settlement");
		}*/
			}
		}
		
		return bestCell;
	}

	
	private int getPrivateLandDealCode(int strike, double dealBreakingProbability) {
		int dealCode = -99;
		double prob1 = getRandomInRange(1, 0.00D, 1.00D);
		if(prob1 > dealBreakingProbability) {  // NO DEAL to enter private land
			if(strike == 2)  // dropout of pastoralist cycle
				dealCode = 0;
			else          // find new location to go
				dealCode = -1;
		} else 
			dealCode = 1; // Yes to enter private land
		
		return dealCode;
	}
	
	
	Map<Double, String> getAdditiveModelScore(List<String> candidateLocations) {
		Map<Double, String> scoreVsLocation = new HashMap<Double, String>();
		somalilandGeo = SomalilandContextCreator.SomalilandGeographyObject;
		try {
			GridCoverage2D c1 = somalilandGeo.getCoverage(ResourceConstants.MODIS_LAYER);
			GridCoverage2D c3 = somalilandGeo.getCoverage(ResourceConstants.MAN_MADE_WATER_LAYER);
			GridCoverage2D c4 = somalilandGeo.getCoverage(ResourceConstants.SLOPE_LAYER);
			
			//GridCoverage2D c5 = somalilandGeo.getCoverage(ResourceConstants.CONFLICT_LAYER);
			
			GridCoverage2D conflictCvg1 = somalilandGeo.getCoverage(ResourceConstants.SEASONAL_CONFLICT_LAYER_MINUS_1);
			GridCoverage2D conflictCvg2 = somalilandGeo.getCoverage(ResourceConstants.SEASONAL_CONFLICT_LAYER);
			
			GridCoverage2D c2 = null;
			if(SomalilandContextCreator.currentSeason.equals(ResourceConstants.GU_SPRING) ||
					SomalilandContextCreator.currentSeason.equals(ResourceConstants.DEYR_AUTUMN)) {
				c2 = somalilandGeo.getCoverage(ResourceConstants.SURFACE_WATER_LAYER);
			}
			
			for(int i=0; i<candidateLocations.size(); i++) {
				Double lat = Double.parseDouble(candidateLocations.get(i).split(",")[0]);
				Double lon = Double.parseDouble(candidateLocations.get(i).split(",")[1]);
				DirectPosition pos = new DirectPosition2D(lat, lon);
				
				// vegetation modis NDVI index
				double ndvi = ((double[]) c1.evaluate(pos))[0];  
				
				// SAVI Index: https://wiki.landscapetoolbox.org/doku.php/remote_sensing_methods:soil-adjusted_vegetation_index
				double NIR = ((double[]) c1.evaluate(pos))[4]; 
				double RED = ((double[]) c1.evaluate(pos))[3]; 
				double v1= ((NIR-RED) * (1+ResourceConstants.SAVI_INDEX_L)) / (NIR + RED + ResourceConstants.SAVI_INDEX_L);
				
				// activate surface water only in the rainy season
				byte v2 = 0;
				if(SomalilandContextCreator.currentSeason.equals(ResourceConstants.GU_SPRING) ||
						SomalilandContextCreator.currentSeason.equals(ResourceConstants.DEYR_AUTUMN)) {
					v2 = ((byte[]) c2.evaluate(pos))[0];  // surface water
				} 
				
				float v3 = ((float[]) c3.evaluate(pos))[0]; // man-made points
				float v4 = (((float[]) c4.evaluate(pos))[0]); // slope of land
				
				//float v5 = (((float[]) c5.evaluate(pos))[0]); // conflict
				float v5_1 = 0F; 
				Envelope2D env5_1 = conflictCvg1.getEnvelope2D();
				if(env5_1.contains(pos)) {
					v5_1 = ((float[]) conflictCvg1.evaluate(pos))[0] / 2.0F;
				} 
					
				float v5_2 = 0F;
				Envelope2D env5_2 = conflictCvg2.getEnvelope2D();
				if(env5_2.contains(pos)) {
					v5_2 = ((float[]) conflictCvg2.evaluate(pos))[0];
				}

				float v5 = v5_1 + v5_2;
				double score = v1 + v2 + v3 - (0.25 * v4) - v5;
				scoreVsLocation.put(score, candidateLocations.get(i)); 
				System.out.print(ndvi + " ; " + v1 +","+ v2 +"," + v3 +"," + v4 +"," + v5);
				System.out.println(" --> score = " + score + " :: location = " + scoreVsLocation.get(score));
				
			}
			
			/*Map<Double, String> sortedScoreVsLocation = new HashMap<Double, String>();
			List<Double> keyArray = new ArrayList<Double>(scoreVsLocation.keySet());
			Collections.sort(keyArray, Collections.reverseOrder());
			for(Double s : keyArray) {
				sortedScoreVsLocation.put(s, scoreVsLocation.get(s));
			}*/
			
		} catch(PointOutsideCoverageException e) {
			e.printStackTrace();
		}
		
		return scoreVsLocation;
	}

	
	private List<String> radialNeighborhood(double currentLatX, double currentLonY, Integer rangeKM, int pixelResolution) {
		List<String> candidateLocations = new ArrayList<String>();
		Double deltaLatitude = (rangeKM*0.0089);
		Double deltaLongitude = (rangeKM*0.0089/Math.cos(currentLatX*0.018));
		
		double lat1 = currentLatX;
		double lon1 = currentLonY;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX + deltaLatitude;
		lon1 = currentLonY + deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX - deltaLatitude;
		lon1 = currentLonY - deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX + deltaLatitude;
		lon1 = currentLonY - deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX - deltaLatitude;
		lon1 = currentLonY + deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX - deltaLatitude;
		lon1 = currentLonY;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX + deltaLatitude;
		lon1 = currentLonY;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX;
		lon1 = currentLonY + deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX;
		lon1 = currentLonY - deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		// additions for radial neighborhood
		rangeKM = rangeKM + pixelResolution;
		lat1 = currentLatX - deltaLatitude;
		lon1 = currentLonY;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX + deltaLatitude;
		lon1 = currentLonY;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX;
		lon1 = currentLonY + deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX;
		lon1 = currentLonY - deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		
		return getOnlyValidCoords(candidateLocations);
	}


	private List<String> mooreNeighborhood(double currentLatX, double currentLonY, Integer rangeKM) {
		List<String> candidateLocations = new ArrayList<String>();
		Double deltaLatitude = (rangeKM*0.0089);
		Double deltaLongitude = (rangeKM*0.0089/Math.cos(currentLatX*0.018));
		
		double lat1 = currentLatX;
		double lon1 = currentLonY;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX + deltaLatitude;
		lon1 = currentLonY + deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX - deltaLatitude;
		lon1 = currentLonY - deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX + deltaLatitude;
		lon1 = currentLonY - deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX - deltaLatitude;
		lon1 = currentLonY + deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX - deltaLatitude;
		lon1 = currentLonY;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX + deltaLatitude;
		lon1 = currentLonY;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX;
		lon1 = currentLonY + deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		lat1 = currentLatX;
		lon1 = currentLonY - deltaLongitude;
		candidateLocations.add(lat1+","+lon1);
		
		return getOnlyValidCoords(candidateLocations);
	}

	
	private List<String> getOnlyValidCoords(List<String> candidateLocations) {
		List<String> validLocs = new ArrayList<String>();
		
		GridCoverage2D c1 = somalilandGeo.getCoverage(ResourceConstants.MODIS_LAYER);
		GridCoverage2D c2 = somalilandGeo.getCoverage(ResourceConstants.SURFACE_WATER_LAYER);
		GridCoverage2D c3 = somalilandGeo.getCoverage(ResourceConstants.MAN_MADE_WATER_LAYER);
		GridCoverage2D c4 = somalilandGeo.getCoverage(ResourceConstants.SLOPE_LAYER);
		//GridCoverage2D c5 = somalilandGeo.getCoverage(ResourceConstants.CONFLICT_LAYER);
		GridCoverage2D c6 = somalilandGeo.getCoverage(ResourceConstants.BOUNDARY_CONTAINER);
		
		Envelope2D e1 = c1.getEnvelope2D();
		Envelope2D e2 = c2.getEnvelope2D();
		Envelope2D e3 = c3.getEnvelope2D();
		Envelope2D e4 = c4.getEnvelope2D();
		//Envelope2D e5 = c5.getEnvelope2D();
		Envelope2D e6 = c6.getEnvelope2D();
		
		for(String coords : candidateLocations) {
			double lat = Double.parseDouble(coords.split(",")[0]);
			double lon = Double.parseDouble(coords.split(",")[1]);
			DirectPosition pos = new DirectPosition2D(lat, lon);
			if(e6.contains(pos) && e1.contains(pos) && e2.contains(pos) 
					&& e3.contains(pos) && e4.contains(pos) ) {
					//&& e5.contains(pos)) {
				double v1 = ((double[]) c1.evaluate(pos))[0];  //ndvi
				float v3 = ((float[]) c3.evaluate(pos))[0]; // man-made points
				float v4 = (((float[]) c4.evaluate(pos))[0]); // slope of land
				//float v5 = (((float[]) c5.evaluate(pos))[0]); // conflict
				byte v6 = ((byte[]) c6.evaluate(pos))[0];  
				
				if(v6 == 0 && v1 != Double.NaN && v3 != Float.NaN && v4 != Float.NaN  
						&& v1 >= 0.0 && v3 >= 0 && v4 >= 0)
					validLocs.add(coords);
			} 
		}
		
		
		return validLocs;
	}

	
	private void updateSeasonalLayers() {
		String modisPath = "D:\\HHI2019\\data\\NDVI_Files\\"; 
		String conflictPath = "D:\\HHI2019\\data\\conflict\\";
		String modisFile = "", seasonalConflictFile = "";
		switch (SomalilandContextCreator.currentSeason) {
			case ResourceConstants.JILAAL_WINTER:
				modisFile = modisPath + "DecMarch" + SomalilandContextCreator.currentDate.getYear() + ".tif";
				seasonalConflictFile = conflictPath + "Final_Con1_J_" + SomalilandContextCreator.currentDate.getYear() + ".tif";
				break;
			case ResourceConstants.GU_SPRING:
				modisFile = modisPath + "AprJune" + SomalilandContextCreator.currentDate.getYear() + ".tif";
				seasonalConflictFile = conflictPath + "Final_Con1_G_" + SomalilandContextCreator.currentDate.getYear() + ".tif";
				break;
			case ResourceConstants.HAGAAR_SUMMER:
				modisFile = modisPath + "JulySept" + SomalilandContextCreator.currentDate.getYear() + ".tif";
				seasonalConflictFile = conflictPath + "Final_Con1_H_" + SomalilandContextCreator.currentDate.getYear() + ".tif";
				break;
			case ResourceConstants.DEYR_AUTUMN:
				modisFile = modisPath + "OctNov" + SomalilandContextCreator.currentDate.getYear() + ".tif";
				seasonalConflictFile = conflictPath + "Final_Con1_D_" + SomalilandContextCreator.currentDate.getYear() + ".tif";
				break;
			default:
				System.out.println("******** ERROR: CANNOT FIND MODIS NDVI FILE");
				break;
		}
		
		System.out.println("Reading MODIS NDVI VEGETATION raster file : seasonal layer");
		somalilandGeo.addCoverage(ResourceConstants.MODIS_LAYER, utils.loadRasterFile(modisFile));

		System.out.println("Reading Seasonal Conflict raster file : seasonal layer");
		somalilandGeo.addCoverage(ResourceConstants.SEASONAL_CONFLICT_LAYER, utils.loadRasterFile(seasonalConflictFile));
		
		
	}



}
