package somalilandPastoralistMovement.modeller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.opengis.feature.simple.SimpleFeature;

import somalilandPastoralistMovement.agents.Pastoralist;

public class Utils {
	
	GridCoverage2D loadRasterFile(String geoTiffRasterFilename) {
		try {
	    	GeoTiffReader geoTiffReader = new GeoTiffReader(new File(geoTiffRasterFilename));
	    	// This method reads in the TIFF image, constructs an appropriate CRS, 
	    	// determines the math transform from raster to the CRS model, and constructs a GridCoverage.
	    	GridCoverage2D coverage = (GridCoverage2D) geoTiffReader.read(null);
	    	return coverage;
		} catch (DataSourceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	List<SimpleFeature> loadFeaturesFromShapefile(String filename){
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		URL url = null;
		try {
			url = new File(filename).toURL();
			SimpleFeatureIterator fiter = null;
			ShapefileDataStore store = null;
			store = new ShapefileDataStore(url);
			fiter = store.getFeatureSource().getFeatures().features();
			while(fiter.hasNext()){
				features.add(fiter.next());
			}
			fiter.close();
			store.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return features;
	}


	public void writeOutput(List<Pastoralist> pastoralists, int iteration, String basePath) {
		System.out.println("Writing simulation output");
		String newline = "\n";
		try {
			for(Pastoralist p : pastoralists) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(basePath+"run-"+iteration+"-pastoralist-"+p.getPastoralistId()+".csv"));
				bw.write("tickno,lat,lon,score,strikes,scoutRange"+newline);
				int tickno = 0;
				for(String coords_score : p.getLatLongPerTick()) {
					coords_score = coords_score.substring(1, coords_score.length()-2);
					bw.write(tickno + "," + coords_score+newline);
					tickno++;
				}
				bw.flush();
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
