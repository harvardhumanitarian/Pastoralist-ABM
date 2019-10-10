package somalilandPastoralistMovement.modeller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.DirectPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

import repast.simphony.gis.util.GeometryUtil;
import repast.simphony.query.space.gis.WithinQuery;
import repast.simphony.space.gis.Geography;
import somalilandPastoralistMovement.agents.BareSoilNWater;
import somalilandPastoralistMovement.agents.EthnicityClan;
import somalilandPastoralistMovement.agents.Pastoralist;
import somalilandPastoralistMovement.agents.PrivateLandSettlemets;


/**
 * Generates pastoralist agents.
 * @author Swapna
 *
 */
public class AgentGenerator {
	
	Utils utils = new Utils();
	Geography SomalilandGeographyObject;
	GridCoverage2D c6;
	GridCoverage2D c1;
	GridCoverage2D c2;
	GridCoverage2D c3;
	GridCoverage2D c4;
	GridCoverage2D c5;

	Envelope2D e1;
	Envelope2D e2;
	Envelope2D e3;
	Envelope2D e4;
	Envelope2D e5;
	Envelope2D e6;

	
	public static void main(String args[]) {
		float samplingPercent = 0.1f;
		int noSamples = 100;
		String parentdir = "D:\\HHI2019\\data\\";
		String fname = parentdir + "pastoralist_v3\\pastrolists-v3.csv";
		String secParentDir = "D:\\HHI2019\\data\\pastoralist_v3\\";
		
		writeAdminWisePopFiles(fname, secParentDir);
		
		//sampledPastoralistFile(fname,samplingPercent,secParentDir);
	}
	
	
	private static void sampledPastoralistFile(String fname, float samplingPercent, String secParentDir) {

		Map<String,Integer> admin1VsNomadHH = new HashMap<String,Integer>();
		admin1VsNomadHH.put("Awdal", 28511);
		admin1VsNomadHH.put("Woqooyi Galbeed", 43741);
		admin1VsNomadHH.put("Togdheer", 24285);
		admin1VsNomadHH.put("Sool", 28985);
		admin1VsNomadHH.put("Sanaag", 47764);
		admin1VsNomadHH.put("Bari", 19114);
		admin1VsNomadHH.put("Nugaal", 33367);
		
		int noAgents = (int) (admin1VsNomadHH.get("Awdal") * samplingPercent);
		
		try {
			//Files.readAllLines(Paths.get("D:\\HHI2019\\data\\pastrolists-v2.csv")).stream().filter(row -> !row.startsWith("id")).forEach(row -> {
			BufferedReader br = new BufferedReader(new FileReader(fname));
			String row = "";	
			int k=1;
			br.readLine(); //header
			while((row = br.readLine()) != null) {
				String[] pts = row.split(",");
				String admin1 = pts[3];
					
				Pastoralist p =new Pastoralist(k);
				String currentLoc = Double.parseDouble(pts[1]) + "," + Double.parseDouble(pts[2]);
				List<String> initialLocation = new ArrayList<String>();
				initialLocation.add(0, currentLoc);
				p.setLatLongPerTick(initialLocation);
				p.setOriginAdmin1Level(admin1);
				p.setOriginEthnicity(pts[4]);
				p.setOriginClan(pts[5]);
				
				k++;
			}
			
			br.close();
			System.out.println("Total Agents = " + (k-1));
			
			
			//});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static void writeAdminWisePopFiles(String fname, String secParentDir) {
		Map<String,Integer> admin1VsNomadHH = new HashMap<String,Integer>();
		admin1VsNomadHH.put("Awdal", 28511);
		admin1VsNomadHH.put("Woqooyi Galbeed", 43741);
		admin1VsNomadHH.put("Togdheer", 24285);
		admin1VsNomadHH.put("Sool", 28985);
		admin1VsNomadHH.put("Sanaag", 47764);
		admin1VsNomadHH.put("Bari", 19114);
		admin1VsNomadHH.put("Nugaal", 33367);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fname));
			String row = "";	
			int k=1;
			String header = br.readLine(); //header
			String currentAdmin1 = "Bari";
			List<String> adminRows = new ArrayList<String>();
			while((row = br.readLine()) != null) {
				String[] pts = row.split(",");
				String admin1 = pts[3];
				if(!currentAdmin1.equals(admin1)) {
					System.out.println("Writing " + currentAdmin1);
					Files.write(Paths.get(secParentDir+currentAdmin1+".csv"), adminRows);
					
					currentAdmin1 = admin1;
					adminRows = new ArrayList<String>();
					adminRows.add(header);
				}
				
				adminRows.add(row);
				
			}
			
			System.out.println("Writing " + currentAdmin1);
			Files.write(Paths.get(secParentDir+currentAdmin1+".csv"), adminRows);
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * Generate agents at Admin 1 level that lie in somaliland
	 */
	void GeneratePastoralistsAgents() {
		SomalilandGeographyObject = SomalilandContextCreator.SomalilandGeographyObject;
		
		c6 = SomalilandGeographyObject.getCoverage(ResourceConstants.BOUNDARY_CONTAINER);
		c1 = SomalilandGeographyObject.getCoverage(ResourceConstants.MODIS_LAYER);
		c2 = SomalilandGeographyObject.getCoverage(ResourceConstants.SURFACE_WATER_LAYER);
		c3 = SomalilandGeographyObject.getCoverage(ResourceConstants.MAN_MADE_WATER_LAYER);
		c4 = SomalilandGeographyObject.getCoverage(ResourceConstants.SLOPE_LAYER);
		c5 = SomalilandGeographyObject.getCoverage(ResourceConstants.CONFLICT_LAYER);

		e1 = c1.getEnvelope2D();
		e2 = c2.getEnvelope2D();
		e3 = c3.getEnvelope2D();
		e4 = c4.getEnvelope2D();
		e5 = c5.getEnvelope2D();
		e6 = c6.getEnvelope2D();

		//Population-Estimation-Survey-of-Somalia-PESS-2013-2014.pdf
		Map<String,Integer> admin1VsNomadHH = new HashMap<String,Integer>(); //225.5k nomad hh
		
		try {
		admin1VsNomadHH.put("Awdal", 28511);
		admin1VsNomadHH.put("Woqooyi Galbeed", 43741);
		admin1VsNomadHH.put("Togdheer", 24285);
		admin1VsNomadHH.put("Sool", 28985);
		admin1VsNomadHH.put("Sanaag", 47764);
		admin1VsNomadHH.put("Bari", 19114);
		admin1VsNomadHH.put("Nugaal", 33367);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\HHI2019\\data\\pastrolists-v-2.csv"));
		bw.write("id,latX,lonY,admin1,ethnicity,clan\n");
		long idnum = 1;
		String somaliaAdmin1filename = "D:\\HHI2019\\data\\Som_Adm\\Somaliland_Adm1.shp";
		List<SimpleFeature> features = utils.loadFeaturesFromShapefile(somaliaAdmin1filename);
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			if (!geom.isValid()){
				System.out.println("Invalid geometry: " + feature.getID());
			}	else if (geom instanceof MultiPolygon){
				String admin1Name = (String)feature.getAttribute("admin1Name");
				if(admin1VsNomadHH.containsKey(admin1Name)) {
					/*MultiPolygon mp = (MultiPolygon)feature.getDefaultGeometry();
					geom = (Polygon)mp.getGeometryN(0);*/
					int failures = 0;
					geom = (Geometry) feature.getDefaultGeometry();
					recursiveAgentGeneration(bw, geom, admin1Name, admin1VsNomadHH.get(admin1Name),idnum,failures);
				}
			}
		}
		System.out.println("Done. Total number of agents = " + idnum);
		bw.flush();
		bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * Generate agents with constraints such as no agent generation in 
	 * 1. private settlements
	 * 2. bare soil areas
	 * 3. water bodies
	 * All agents are strictly generated within Somaliland geography.
	 * 
	 * @param bw
	 * @param geom
	 * @param admin1Name
	 * @param noAgents
	 * @param startIdnum
	 * @param failures
	 * @throws IOException
	 */
	private void recursiveAgentGeneration(BufferedWriter bw, Geometry geom, String admin1Name, Integer noAgents, long startIdnum, int failures) throws IOException {
		List<Coordinate> agentCoords = GeometryUtil.generateRandomPointsInPolygon(geom, noAgents);
		System.out.println(admin1Name + " will generate  " + agentCoords.size() + " nomad households.");
		for(Coordinate coord : agentCoords) {
			// check if the point is inside coverage and if the point is NOT in private settlement
			if(isAgentCoordValid(coord)) {
				System.out.println("PID :: " + coord.x + "," + coord.y);
				Pastoralist p =new Pastoralist(startIdnum);
				List<String> initialLocation = new ArrayList<String>();
				initialLocation.add(0, coord.x + "," + coord.y);
				p.setLatLongPerTick(initialLocation);
				p.setOriginAdmin1Level(admin1Name);
				EthnicityClan ec = getAgentEthnicity(coord);
				p.setOriginEthnicity(ec.getEthnicityName());
				p.setCurrentClan(ec.getClanName());
				bw.write(p.getPastoralistId() + ","
						+ coord.x + ","
						+ coord.y + ","
						+ p.getOriginAdmin1Level() + ","
						+ p.getOriginEthnicity() + ","
						+ p.getCurrentClan()
						+"\n");
				//context.add(p);
				startIdnum++;
			} else {
				failures = failures+1;
			}
		}
		
		if(failures > 0) {
			// re-create agents
			recursiveAgentGeneration(bw, geom, admin1Name, failures, startIdnum, 0);
		}
		
	}


	private EthnicityClan getAgentEthnicity(Coordinate coord) {
		EthnicityClan e = null;
		GeometryFactory fac = new GeometryFactory();
		Geometry geom = fac.createPoint(coord);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		WithinQuery geoQuery1 = new WithinQuery(SomalilandGeographyObject, geom);
		for (Object obj : geoQuery1.query()) {
			if (obj instanceof EthnicityClan){
				e = (EthnicityClan) obj;
				break;
			} 
		}
		return e;
	}


	private boolean isAgentCoordValid(Coordinate coord) {
		boolean flag = false, isPrivateSettlement = false, hasEthnicity = false, isBareSoilWater=false;
		
		

		GeometryFactory fac = new GeometryFactory();
		Geometry geom = fac.createPoint(coord);
		
		// agent shouldnt be a part of private settlement, start in bare soil areas or be located in a water body :D
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		WithinQuery geoQuery1 = new WithinQuery(SomalilandGeographyObject, geom);
		for (Object obj : geoQuery1.query()) {
			if (obj instanceof PrivateLandSettlemets){
				isPrivateSettlement = true;
				break;
			} 
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		WithinQuery geoQuery3 = new WithinQuery(SomalilandGeographyObject, geom);
		for (Object obj : geoQuery3.query()) {
			if (obj instanceof BareSoilNWater){
				isBareSoilWater = true;
				break;
			} 
		}
		
		// check if has valid ethnic description (this check because of the shapefile discrepancy)
		@SuppressWarnings({ "rawtypes", "unchecked" })
		WithinQuery geoQuery2 = new WithinQuery(SomalilandGeographyObject, geom);
		for (Object obj : geoQuery2.query()) {
			if (obj instanceof EthnicityClan){
				hasEthnicity= true;
				break;
			} 
		}
		
		DirectPosition pos = new DirectPosition2D(coord.x, coord.y);
		if(e1.contains(pos) && e2.contains(pos) && e3.contains(pos) && e4.contains(pos)
				&& e5.contains(pos) && isPrivateSettlement==false && hasEthnicity == true && isBareSoilWater==false) {
			flag = true;
		} 
		
		return flag;
	}
	
	

}
