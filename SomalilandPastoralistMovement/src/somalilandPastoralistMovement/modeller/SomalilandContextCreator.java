package somalilandPastoralistMovement.modeller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.geotools.coverage.grid.GridCoverage2D;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import somalilandPastoralistMovement.agents.Admin2Level;
import somalilandPastoralistMovement.agents.BareSoilNWater;
import somalilandPastoralistMovement.agents.Coflict;
import somalilandPastoralistMovement.agents.EthnicityClan;
import somalilandPastoralistMovement.agents.Pastoralist;
import somalilandPastoralistMovement.agents.PrivateLandSettlemets;
import somalilandPastoralistMovement.agents.StrategicWaterPoints;

public class SomalilandContextCreator implements ContextBuilder<Object> {

	static Map<Month, String> monthVsSeason = new HashMap<Month, String>();
	static int totalTicks, iteration, minScoutingRange, maxScoutingRange;
	static float samplingPercent;
	static String currentSeason;
	
	static String parentdir = "/n/holyscratch01/vanrooyen_lab/simulation_input/data1/data/data/";
	//static String parentdir = "D:\\HHI2019\\data\\";
	//static String parentdir = "C:\\Users\\saira\\Desktop\\SimulationData\\data\\data\\";
	//static String parentdir = "C:\\HHI2019\\data\\";
	
	static String currentRunAdmin1 = "WoqooyiGalbeed";   // Awdal ; WoqooyiGalbeed ; Togdheer ; Sool ; Sanaag ; Bari ; Nugaal
	
	static LocalDate currentDate;
	static Geography<Object> SomalilandGeographyObject;
	Utils utils = new Utils();
	
	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("SomalilandPastoralistMovement");
		iteration = 1;
		GeographyParameters<Object> geoParams = new GeographyParameters<Object>();
		//geoParams.setCrs("EPSG:32638");
		SomalilandGeographyObject = GeographyFactoryFinder.createGeographyFactory(null)
				.createGeography("SomalilandGeography", context, geoParams);
		
		System.out.println("SomalilandGeographyObject.getCRS() = " + SomalilandGeographyObject.getCRS());
		
		initializeParams();
		loadInitialLayers(context);
		context.add(SomalilandGeographyObject);
		
		// Primary updater agent with step() with priority=1
		Updater u = new Updater();
		context.add(u);
		// Pastoralist agents
		System.out.println("Loading pastoralist agent data");
		//loadPastoralistAgentData(context);
		loadPastoralistAgentDataFromCSV(context);
		
		
		/*
		AgentGenerator ag = new AgentGenerator();
		context.add(ag);
		ag.GeneratePastoralistsAgents();
		RunEnvironment.getInstance().endRun();
		 */
		
		/*GridCoverage2D c6 = SomalilandGeographyObject.getCoverage(ResourceConstants.BOUNDARY_CONTAINER);
		DirectPosition pos2 = new DirectPosition2D(43.314576561699994,10.562698003396227);
		byte[] v1 = (byte[]) c6.evaluate(pos2);
		for(int i=0; i<v1.length; i++)
			System.out.println(v1[i]);*/
		
		
		/*
		
		String f2 = "D:\\HHI2019\\data\\NDVI_Files\\OctNov2013.tif";
		GridCoverage2D coverage2 = utils.loadRasterFile(f2);
		SomalilandGeographyObject.addCoverage("try2", coverage2);
		GridCoverage2D c2 = SomalilandGeographyObject.getCoverage("try2");
		DirectPosition pos2 = new DirectPosition2D(43.314576561699994,10.562698003396227);
		double[] v1 = (double[]) c2.evaluate(pos2);
		for(int i=0; i<v1.length; i++)
			System.out.println(v1[i]);
		Envelope2D e1 = c1.getEnvelope2D();
		Envelope2D e2 = c2.getEnvelope2D();
		pos = new DirectPosition2D(43.66167656169999,11.01381750875223);
		if(e2.contains(pos)) {
			System.out.println("valid");
		} 
		if(e1.contains(pos)) {
			System.out.println("valid");
		} 
		*/
	
		return context;
	}

	
	private void loadPastoralistAgentDataFromCSV(Context<Object> context) {
		Map<String,Integer> admin1VsNomadHH = new HashMap<String,Integer>();
		admin1VsNomadHH.put("Awdal", 28511);
		admin1VsNomadHH.put("WoqooyiGalbeed", 43741);
		admin1VsNomadHH.put("Togdheer", 24285);
		admin1VsNomadHH.put("Sool", 28985);
		admin1VsNomadHH.put("Sanaag", 47764);
		admin1VsNomadHH.put("Bari", 19114);
		admin1VsNomadHH.put("Nugaal", 33367);
		System.out.println(samplingPercent);
		int noAgents = (int) (admin1VsNomadHH.get(currentRunAdmin1) * samplingPercent);
		System.out.println(noAgents);
		Set<Integer> indices = getRandomIndices(noAgents, admin1VsNomadHH.get(currentRunAdmin1)); //new ArrayList<Integer>();
		System.out.println(indices);
		Updater u = new Updater();
		GeometryFactory fac = new GeometryFactory();
		try {
			//BufferedReader br = new BufferedReader(new FileReader(parentdir + "pastoralist_v3\\pastrolists-v3.csv"));
			//BufferedReader br = new BufferedReader(new FileReader(parentdir + "pastoralist_v3\\"+currentRunAdmin1+".csv"));
			BufferedReader br = new BufferedReader(new FileReader(parentdir + "pastoralist_v3/"+currentRunAdmin1+".csv"));
			String row = "";	
			int k=-1;
			br.readLine(); //header
			while((row = br.readLine()) != null) {
				k++;
				if(indices.contains(k)) {
					String[] pts = row.split(",");
					String admin1 = pts[3]; //id,latX,lonY,admin1,ethnicity,clan
	/*
					int totalSum = 0;
					List<String> adminKeys = new ArrayList<String>(admin1VsNomadHH.keySet());
					for(String ak : adminKeys) {
						totalSum +=  admin1VsNomadHH.get(ak);
					}
					if(totalSum <= 0)
						break;
					Integer ctr = admin1VsNomadHH.get(admin1);
					if(ctr == 0) {
						admin1VsNomadHH.put(admin1, 0);
						continue;
					}
					ctr = ctr-1;
					admin1VsNomadHH.put(admin1, ctr);
	*/				
					Pastoralist p =new Pastoralist(Integer.parseInt(pts[0]));
					String currentLoc = Double.parseDouble(pts[1]) + "," + Double.parseDouble(pts[2]);
					List<String> initialLocation = new ArrayList<String>();
					initialLocation.add(0, currentLoc);
					p.setLatLongPerTick(initialLocation);
					p.setOriginAdmin1Level(admin1);
					p.setOriginEthnicity(pts[4]);
					p.setOriginClan(pts[5]);
					
					// get initial location score
					System.out.println("currentLoc "+currentLoc);
					Map<Double, String> scoreLocMap = u.getAdditiveModelScore(p.getLatLongPerTick());
					List<Double> keyArray = new ArrayList<Double>(scoreLocMap.keySet());
					//Collections.sort(keyArray, Collections.reverseOrder());
					initialLocation.set(0, scoreLocMap.get(keyArray.get(0)) + "," + keyArray.get(0) + ",0,0" ); //lat,lon,score,strike,scoutRange
					p.setLatLongPerTick(initialLocation);
					context.add(p);
					currentLoc = scoreLocMap.get(keyArray.get(0));
					Geometry geom = fac.createPoint(new Coordinate(Double.parseDouble(currentLoc.split(",")[0]), Double.parseDouble(currentLoc.split(",")[1])));
					SomalilandGeographyObject.move(p, geom);
					
					/*Double lat = Double.parseDouble(p.getLatLongPerTick().get(0).split(",")[0]);
					Double lon = Double.parseDouble(p.getLatLongPerTick().get(0).split(",")[1]);
					initialLocation.set(0, lat + "," + lon + ",0,0,0" ); //lat,lon,score,strike,scoutRange
					p.setLatLongPerTick(initialLocation);
					context.add(p);
					Geometry geom = fac.createPoint(new Coordinate(lat, lon));
					SomalilandGeographyObject.move(p, geom);*/
					
				}
			}
			br.close();
			System.out.println("Total Agents = " + noAgents + " for " + currentRunAdmin1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void loadInitialLayers(Context<Object> context) {
		
		System.out.println("loadInitialLayers()");
		
		// bare soil/water shp
		System.out.println("Loading bare soil and  water data");
		//String soilWaterShpfile =  parentdir+"Som_BareSoil_Water\\Som_BareSoil_Water.shp";   
		String soilWaterShpfile =  parentdir+"Som_BareSoil_Water/Som_BareSoil_Water.shp";
		loadBareSoilWaterShp(soilWaterShpfile, context);
		
		// Load admin2 layer into the geography Somaliland_Adm2.shp
		System.out.println("Loading admin2level data");
		//String somaliaAdmin2filename = parentdir + "Som_Adm\\Somaliland_Adm2.shp";
		String somaliaAdmin2filename = parentdir + "Som_Adm/Somaliland_Adm2.shp";
		loadAdmin2Layer(somaliaAdmin2filename, context);
		
		// ethnicity/clan shp
		System.out.println("Loading Ethnicity/Clan data");
		//String ethnicityfilename =  parentdir + "Som_Ethnicity_Polygon\\Som_Ethnicity_Updated.shp";   //"D:\\HHI2019\\data\\Som_Ethicity\\Som_Ethicity.shp";
		String ethnicityfilename =  parentdir + "Som_Ethnicity_Polygon/Som_Ethnicity_Updated.shp";   
		loadEthnicityData(ethnicityfilename, context);
		
		// livelihood zones (Somaliland_LivelihoodZones.shp) : static : 
		//String livelihoodFilename = parentdir + "Som_LivelihoodZones\\Somaliland_LivelihoodZones.shp";
		String livelihoodFilename = parentdir + "Som_LivelihoodZones/Somaliland_LivelihoodZones.shp";
		loadLivelihoodZones(livelihoodFilename, context);
		
		// private land settlements buffer (Settlements_4k_14k_clip.shp) : static
		//String privateLandFile = parentdir + "Som_PrivateLand\\Settlements_4k_14k_clip.shp";
		String privateLandFile = parentdir + "Som_PrivateLand/Settlements_4k_14k_clip.shp";
		loadPrivateLandSettlements(privateLandFile, context);
		
		// load man-made water points : static 
		System.out.println("Man-made strategic water sources");	
		loadAllStrategicWaterPoints(context);
		
		// conflict points : static -> load beyond start year
		//System.out.println("Conflict layer : yearly");
		//String conflictFile = parentdir + "conflict\\Somaliland_Conflict.shp";
		//String conflictFile = parentdir + "conflict/Somaliland_Conflict.shp";
		//loadConflictData(conflictFile, context);
		
		// Ethnic buffers for potential conflict
		System.out.println("Raster for Ethnic buffers for potential conflict");
		//GridCoverage2D coverage7 = utils.loadRasterFile(parentdir + "Modified Files\\EthnicityBuffer_1.tif");
		GridCoverage2D coverage7 = utils.loadRasterFile(parentdir + "Modified Files/EthnicityBuffer_1.tif");
		SomalilandGeographyObject.addCoverage(ResourceConstants.ETHNIC_BUFFER, coverage7);
		
		// boundary containment raster of somaliland
		System.out.println("Boundary containment raster of somaliland.");
		//GridCoverage2D coverage6 = utils.loadRasterFile(parentdir + "boundraster1\\boundraster1.tif");
		GridCoverage2D coverage6 = utils.loadRasterFile(parentdir + "boundraster1/boundraster1.tif");
		SomalilandGeographyObject.addCoverage(ResourceConstants.BOUNDARY_CONTAINER, coverage6);
		
		// conflict 
		//System.out.println("Reading conflict 2009-2019 raster file.");
		//GridCoverage2D coverage5 = utils.loadRasterFile(parentdir + "conflict_2009-2019_raster_normalized\\conflict_2009-2019_raster_normalized.tif");
		//GridCoverage2D coverage5 = utils.loadRasterFile(parentdir + "conflict_2009-2019_raster_normalized/conflict_2009-2019_raster_normalized.tif");
		//SomalilandGeographyObject.addCoverage(ResourceConstants.CONFLICT_LAYER, coverage5);
		
		// slope 
		System.out.println("Reading slope raster file.");
		//GridCoverage2D coverage4 = utils.loadRasterFile(parentdir + "som_slope_normalized_tif\\som_slope_normalized.tif");//("D:\\HHI2019\\data\\som_slope_v2\\som_slope.tif");
		GridCoverage2D coverage4 = utils.loadRasterFile(parentdir + "som_slope_normalized_tif/som_slope_normalized.tif");
		SomalilandGeographyObject.addCoverage(ResourceConstants.SLOPE_LAYER, coverage4);
		
		// Man-made water points
		System.out.println("Reading man-made water points raster file.");
		//GridCoverage2D coverage3 = utils.loadRasterFile(parentdir + "som_normalized_waterpoints\\som_normalized_waterpoints.tif");
		GridCoverage2D coverage3 = utils.loadRasterFile(parentdir + "som_normalized_waterpoints/som_normalized_waterpoints.tif");
		SomalilandGeographyObject.addCoverage(ResourceConstants.MAN_MADE_WATER_LAYER, coverage3);
		
		// Surface water data :: TODO get 2 rasters - dry and wet season. Load both of them in different layers and alter between
		// them using current season indicator
		System.out.println("Reading Surface water raster file ");
		//GridCoverage2D coverage2 = utils.loadRasterFile(parentdir + "SurfaceWater\\SurfaceWaterSomalia.tif");
		GridCoverage2D coverage2 = utils.loadRasterFile(parentdir + "SurfaceWater/SurfaceWaterSomalia.tif");
		SomalilandGeographyObject.addCoverage(ResourceConstants.SURFACE_WATER_LAYER, coverage2);
		
		// MODIS NDVI VEGETATION : seasonal layer
		Parameters params = RunEnvironment.getInstance().getParameters();
		currentSeason = monthVsSeason.get(Month.of((Integer)params.getValue("startMonth")));
		//String modisPath = parentdir + "NDVI_Files\\"; 
		//String conflictPath = parentdir + "conflict\\";
		String modisPath = parentdir + "NDVI_Files/"; 
		String conflictPath = parentdir + "conflict/";
		String modisFile = "", seasonalConflictFile = "";
		switch (currentSeason) {
			case ResourceConstants.JILAAL_WINTER:
				modisFile = modisPath + "DecMarch" + currentDate.getYear() + ".tif";
				seasonalConflictFile = conflictPath + "Final_Con1_J_" + currentDate.getYear() + ".tif";
				break;
			case ResourceConstants.GU_SPRING:
				modisFile = modisPath + "AprJune" + currentDate.getYear() + ".tif";
				seasonalConflictFile = conflictPath + "Final_Con1_G_" + currentDate.getYear() + ".tif";
				break;
			case ResourceConstants.HAGAAR_SUMMER:
				modisFile = modisPath + "JulySept" + currentDate.getYear() + ".tif";
				seasonalConflictFile = conflictPath + "Final_Con1_H_" + currentDate.getYear() + ".tif";
				break;
			case ResourceConstants.DEYR_AUTUMN:
				modisFile = modisPath + "OctNov" + currentDate.getYear() + ".tif";
				seasonalConflictFile = conflictPath + "Final_Con1_D_" + currentDate.getYear() + ".tif";
				break;
			default:
				System.out.println("******** ERROR: CANNOT FIND MODIS NDVI FILE");
				break;
		}
		
		// this video explains the value of the different bands: https://youtu.be/YP0et8l_bvY
		//AT https://developers.google.com/earth-engine/datasets/catalog/modis 
		// look for dataset MOD13Q1.006 Terra Vegetation Indices 16-Day Global 250m : description mentions that band 1 = NDVI, band 2 = EVI
		// Then navigate to
		// https://developers.google.com/earth-engine/datasets/catalog/MODIS_006_MOD13Q1
		// The band descriptions confirm our understanding and crosscheck NDVI calculation by using v[3]=Red band :: v[4] = NIR band
		//System.out.println("NDVI index = " + v[0]); 	
		System.out.println("Reading MODIS NDVI VEGETATION raster file : seasonal layer : "  + modisFile);
		GridCoverage2D coverage1 = utils.loadRasterFile(modisFile);
		SomalilandGeographyObject.addCoverage(ResourceConstants.MODIS_LAYER, coverage1);
		
		System.out.println("Reading Seasonal Conflict raster file : seasonal layer : " + seasonalConflictFile);
		SomalilandGeographyObject.addCoverage(ResourceConstants.SEASONAL_CONFLICT_LAYER, utils.loadRasterFile(seasonalConflictFile));
		
		System.out.println("Reading Seasonal Conflict raster file : seasonal layer - 1 : " + seasonalConflictFile);
		SomalilandGeographyObject.addCoverage(ResourceConstants.SEASONAL_CONFLICT_LAYER_MINUS_1, 
				utils.loadRasterFile(seasonalConflictFile));
		
	}
	
	
	private Set<Integer> getRandomIndices(int noAgents, int totalAgents) {
		Set<Integer> indices = new HashSet<Integer>(noAgents);
		Random r = new Random();
		int k=0;
		while(true) {
			int ridIndex = r.nextInt(totalAgents);
			if(!indices.contains(ridIndex)) {
				k++;
				indices.add(ridIndex);
			}
			//if(k==noAgents)
			if(k==5)
				break;
		}
		
		return indices;
	}

	
	private void loadBareSoilWaterShp(String soilWaterShpfile, Context<Object> context) {
		List<SimpleFeature> features = utils.loadFeaturesFromShapefile(soilWaterShpfile);
		int key = 1;
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			if (geom instanceof MultiPolygon) {
				geom = (Geometry) feature.getDefaultGeometry();
				BareSoilNWater b = new BareSoilNWater(key);
		        context.add(b);
		        SomalilandGeographyObject.move(b, geom);
		        key++;
			} else {
				System.out.println("Invalid geometry: " + feature.getID());
			}
		}
	
		
	}


	private void loadPrivateLandSettlements(String privateLandFile, Context<Object> context) {
		List<SimpleFeature> features = utils.loadFeaturesFromShapefile(privateLandFile);
		int key = 1;
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			if (geom instanceof MultiPolygon) {
				geom = (Geometry) feature.getDefaultGeometry();
				PrivateLandSettlemets p = new PrivateLandSettlemets(key++, (Double)feature.getAttribute("AREA_GEO"));
		        context.add(p);
		        SomalilandGeographyObject.move(p, geom);
			} else {
				System.out.println("Invalid geometry: " + feature.getID());
			}
		}
	}

	
	private void loadLivelihoodZones(String livelihoodFilename, Context<Object> context) {
		List<SimpleFeature> features = utils.loadFeaturesFromShapefile(livelihoodFilename);
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			if (geom instanceof MultiPolygon) {
				geom = (Geometry) feature.getDefaultGeometry();
				String livelihoodZoneName = (String)feature.getAttribute("LZNAMEEN");
		        context.add(livelihoodZoneName);
		        SomalilandGeographyObject.move(livelihoodZoneName, geom);
			} else {
				System.out.println("Invalid geometry: " + feature.getID());
			}
		}
	}

	
	private void initializeParams() {
		System.out.println("Inside initializeParams()");
		try {
			Parameters params = RunEnvironment.getInstance().getParameters();
			int currentMonth = (Integer)params.getValue("startMonth");
			minScoutingRange = (Integer)params.getValue("minScoutingRange");
			maxScoutingRange = (Integer)params.getValue("maxScoutingRange");
			int currentYear = (Integer)params.getValue("startYear");
			int endYear = (Integer)params.getValue("endYear");
			samplingPercent = (float) (((Integer)params.getValue("samplingPercent"))/100f);
			totalTicks = (endYear-currentYear)*12;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
			Date cdate = formatter.parse(currentYear + "-" + currentMonth);
			currentDate = cdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			/*int year  = currentDate.getYear();
			int month = currentDate.getMonthValue();*/
			
			monthVsSeason.put(Month.of(12), ResourceConstants.JILAAL_WINTER);
			monthVsSeason.put(Month.of(1), ResourceConstants.JILAAL_WINTER);
			monthVsSeason.put(Month.of(2), ResourceConstants.JILAAL_WINTER);
			monthVsSeason.put(Month.of(3), ResourceConstants.JILAAL_WINTER);
			monthVsSeason.put(Month.of(4), ResourceConstants.GU_SPRING);
			monthVsSeason.put(Month.of(5), ResourceConstants.GU_SPRING);
			monthVsSeason.put(Month.of(6), ResourceConstants.GU_SPRING);
			monthVsSeason.put(Month.of(7), ResourceConstants.HAGAAR_SUMMER);
			monthVsSeason.put(Month.of(8), ResourceConstants.HAGAAR_SUMMER);
			monthVsSeason.put(Month.of(9), ResourceConstants.HAGAAR_SUMMER);
			monthVsSeason.put(Month.of(10), ResourceConstants.DEYR_AUTUMN);
			monthVsSeason.put(Month.of(11), ResourceConstants.DEYR_AUTUMN);		
		} catch(ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	

	/**
	 * Static layer that does not change with ticks. We load all conflict points beyond the start_year into the geography object
	 * @param conflictFile
	 * @param context
	 */
	private void loadConflictData(String conflictFile, Context<Object> context) {
		System.out.println("loadConflictData() :: " + conflictFile);
		List<SimpleFeature> features = utils.loadFeaturesFromShapefile(conflictFile);
		Parameters parm = RunEnvironment.getInstance().getParameters();
		int startYear = (Integer)parm.getValue("startYear");
		try {
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			if (geom instanceof Point) {
				Long key = (long) feature.getAttribute("year");
				if(key >= startYear) {
					Coflict c =new Coflict((Long)feature.getAttribute("data_id"));
					c.setYear(key.intValue());
					c.setLatX((double) feature.getAttribute("latitude"));
					c.setLonY((double) feature.getAttribute("longitude"));
					c.setAdmin1((String) feature.getAttribute("admin1"));
					c.setAdmin2((String) feature.getAttribute("admin2"));
					DateFormat df = new SimpleDateFormat("dd-MMM-yy");
					c.setEventDate(df.parse((String) feature.getAttribute("event_date")));
					context.add(c);
					SomalilandGeographyObject.move(c, geom);
				}
			} else {
				System.out.println("Invalid geometry: " + feature.getID());
			}
		}
		} catch(ParseException e) {
			e.printStackTrace();
		}
	}

	
	private void loadEthnicityData(String ethnicityfilename, Context<Object> context) {
		List<SimpleFeature> features = utils.loadFeaturesFromShapefile(ethnicityfilename);
		int key = 1;
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			if (geom instanceof MultiPolygon) {
				geom = (Geometry) feature.getDefaultGeometry();
				EthnicityClan e =new EthnicityClan(key++);
				e.setClanName((String)feature.getAttribute("Clan"));
				e.setEthnicityName((String)feature.getAttribute("Ethicity"));
		        context.add(e);
		        SomalilandGeographyObject.move(e, geom);
		        System.out.println("Loading " + e.getClanName() + " :: " + e.getEthnicityName() );
			} else {
				System.out.println("Invalid geometry: " + feature.getID());
			}
		}
	}
	

	private void loadAdmin2Layer(String somaliaAdmin2filename, Context<Object> context) {
		List<SimpleFeature> features = utils.loadFeaturesFromShapefile(somaliaAdmin2filename);
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			if (geom instanceof MultiPolygon) {
				geom = (Geometry) feature.getDefaultGeometry();
				int key = ((Long)feature.getAttribute("OBJECTID_1")).intValue();
				Admin2Level a =new Admin2Level(key);
				a.setAdmin1Name((String)feature.getAttribute("admin1Name"));
				a.setAdmin2Name((String)feature.getAttribute("admin2Name"));
		        context.add(a);
		        SomalilandGeographyObject.move(a, geom);
			} else {
				System.out.println("Invalid geometry: " + feature.getID());
			}
		}
	}
	

	private void loadAllStrategicWaterPoints(Context<Object> context) {
		List<SimpleFeature> features = utils.loadFeaturesFromShapefile(parentdir + "Somaliland_StrategicWaterSources.shp");
		int key = 1;
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			if (geom instanceof Point) {
				geom = (Geometry) feature.getDefaultGeometry();
				StrategicWaterPoints s =new StrategicWaterPoints(key++);
				s.setSourceType((String) feature.getAttribute("Source_Typ"));
				s.setLatX((double) feature.getAttribute("Latitude"));
				s.setLonY((double) feature.getAttribute("Longitude"));
				s.setAdmin1((String) feature.getAttribute("REG_NAME"));
				s.setDistName((String) feature.getAttribute("DIST_NAME"));
				s.setName((String) feature.getAttribute("Name"));
				s.setRecordDate(null);
				if((feature.getAttribute("Record_Dat") != null) && (feature.getAttribute("Record_Dat") != "")) {
					s.setRecordDate((Date) feature.getAttribute("Record_Dat"));
				} 
		        context.add(s);
		        SomalilandGeographyObject.move(s, geom);
			} else {
				System.out.println("Invalid geometry: " + feature.getID());
			}
		}
	}
	
	

/*	private void loadPastoralistAgentData(Context<Object> context) {
		Updater u = new Updater();
		int k=1;
		List<SimpleFeature> features = utils.loadFeaturesFromShapefile("D:\\HHI2019\\data\\pShapfileTemp\\XYpastrolists.shp");
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			if (!geom.isValid()){
				System.out.println("Invalid geometry: " + feature.getID());
			} else if (geom instanceof Point) {
				geom = (Geometry) feature.getDefaultGeometry();
				Pastoralist p =new Pastoralist((Long)feature.getAttribute("id"));
				String currentLoc = (Double)feature.getAttribute("latX") + "," + (Double)feature.getAttribute("lonY");
				List<String> initialLocation = new ArrayList<String>();
				initialLocation.add(0, currentLoc);
				p.setLatLongPerTick(initialLocation);
				p.setOriginAdmin1Level((String)feature.getAttribute("admin1"));
				
				// get initial location score
	        	Double currentLocationScore = (u.getAdditiveModelScore(initialLocation)).get(0);
	        	initialLocation.add(0, currentLoc+","+currentLocationScore);
	        	p.setLatLongPerTick(initialLocation);
	        	
				context.add(p);
				SomalilandGeographyObject.move(p, geom);
				
				if(k==1) //TODO
					break;
				k++;
				
			}
		}
	}*/


}
