package somalilandPastoralistMovement.misc;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		Test t = new Test();
		//t.test1();
		//t.test2();
		//t.latlong();
		//t.testDataTypesArithmetic();
		t.testOrder();
	}

	private void testOrder() {
		List<Double> k = new ArrayList<Double>();
		k.add(27.9);
		k.add(1.0);
		k.add(55.901347);
		k.add(0.00451235);
		k.add(-0.0001235789);
		
		Collections.sort(k, Collections.reverseOrder());
		System.out.println(k);
	}

	private void testDataTypesArithmetic() {
		String v = "0.11125,1,0.0,0.0013482095,65535";
		String[] pts = v.split(",");
		double v1 = Double.parseDouble(pts[0]);
		byte v2_1 = pts[1].getBytes()[0];
		int v2 = v2_1;
		float v3 = Float.parseFloat(pts[2]);
		float v4 = Float.parseFloat(pts[3]);
		
		double score = v1+v2+v3+v4;
		System.out.println(v1 + "," + v2+ "," + v3 + "," + v3);
		System.out.println(score);
		
	}

	private void latlong() {
		// http://www.longitudestore.com/how-big-is-one-gps-degree.html
		// https://stackoverflow.com/questions/7477003/calculating-new-longitude-latitude-from-old-n-meters
			
		double lat_c = 47.291392;
		double lon_c = 10.992806;
		System.out.println("lat_c = " + lat_c + " :: lon_c = "+lon_c);
		
		double rangeKM = 20; // nth order neighborhood
		double pixelResolution = 1;
		
		
		// moore neighborhood
		
		double lat1 = lat_c + (rangeKM*0.0089);
		double lon1 = lon_c + (rangeKM*0.0089/Math.cos(lat_c*0.018));
		//double lon1_1 = lon_c + (rangeKM * 111.3 * Math.cos(lat_c));
		System.out.println("lat1 = " + lat1);
		System.out.println("lon1 = " + lon1);
		//System.out.println("lon1_1 = " + lon1_1);
		
		lat1 = lat_c - (rangeKM*0.0089);
		lon1 = lon_c - (rangeKM*0.0089/Math.cos(lat_c*0.018));
		System.out.println("lat1 = " + lat1);
		System.out.println("lon1 = " + lon1);
		
		lat1 = lat_c + (rangeKM*0.0089);
		lon1 = lon_c - (rangeKM*0.0089/Math.cos(lat_c*0.018));
		System.out.println("lat1 = " + lat1);
		System.out.println("lon1 = " + lon1);
		
		lat1 = lat_c - (rangeKM*0.0089);
		lon1 = lon_c + (rangeKM*0.0089/Math.cos(lat_c*0.018));
		System.out.println("lat1 = " + lat1);
		System.out.println("lon1 = " + lon1);
		
		lat1 = lat_c - (rangeKM*0.0089);
		System.out.println("lat1 = " + lat1);
		System.out.println("lon1 = " + lon_c);
		
		lat1 = lat_c + (rangeKM*0.0089);
		System.out.println("lat1 = " + lat1);
		System.out.println("lon1 = " + lon_c);
		
		lon1 = lon_c + (rangeKM*0.0089/Math.cos(lat_c*0.018));
		System.out.println("lat1 = " + lat_c);
		System.out.println("lon1 = " + lon1);
		
		lon1 = lon_c - (rangeKM*0.0089/Math.cos(lat_c*0.018));
		System.out.println("lat1 = " + lat_c);
		System.out.println("lon1 = " + lon1);
		
		// additions for radial neighborhood
		System.out.println("additions for radial neighborhood");
		rangeKM = rangeKM + pixelResolution;
		lat1 = lat_c - (rangeKM*0.0089);
		System.out.println("lat1 = " + lat1);
		System.out.println("lon1 = " + lon_c);
		
		lat1 = lat_c + (rangeKM*0.0089);
		System.out.println("lat1 = " + lat1);
		System.out.println("lon1 = " + lon_c);
		
		lon1 = lon_c + (rangeKM*0.0089/Math.cos(lat_c*0.018));
		System.out.println("lat1 = " + lat_c);
		System.out.println("lon1 = " + lon1);
		
		lon1 = lon_c - (rangeKM*0.0089/Math.cos(lat_c*0.018));
		System.out.println("lat1 = " + lat_c);
		System.out.println("lon1 = " + lon1);
	}

	private void test2() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		Date cdate;
		try {
			cdate = formatter.parse(2009 + "-" + 1);
			LocalDate currentDate = cdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			/*int year  = currentDate.getYear();
			int month = currentDate.getMonthValue();*/
			System.out.println(currentDate.getYear() + "-" + currentDate.getMonthValue());
			for(int i=1; i<20; i++) {
				currentDate = currentDate.plusMonths(1);
				System.out.println(currentDate.getYear() + "-" + currentDate.getMonthValue());

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	private void test1() {
		String[] months = new DateFormatSymbols().getMonths();
        for (String month : months) {
        	if (!month.isEmpty())
            System.out.println("month = " + month);
        }
        System.out.println(Month.of(3));
	}

	
}
