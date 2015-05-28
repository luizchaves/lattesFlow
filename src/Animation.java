
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import processing.core.*;
import processing.data.Table;
import processing.data.TableRow;

public class Animation extends PApplet {

	PImage map;
	
	float lon1 = (float) -180.0;
	float lat1 = (float) -85.0;
	float lon2 = (float) 180.0;
	float lat2 = (float) 85.0;
	
	int width = 1000;
	int height = 1000;

	MercatorMap mercatorMap;
	
	Table table;
	Map<Integer, List<Flow>> flows = new Hashtable<Integer, List<Flow>>();

	int endYear = 2013;
	int startYear = 1960;
	int currentYear = startYear;
	int resolution = 0;
	int sizeFlows = 0;
	int increment=0;
	int maxTrip = 0;
	
	class Flow{
		public PVector begin;
		public PVector end;
		public int trips;
		
		public Flow(float beginX, float beginY, float endX, float endY, int trips) {
			this.begin = mercatorMap.getScreenLocation(new PVector(beginX, beginY));
			this.end = mercatorMap.getScreenLocation(new PVector(endX, endY));
			this.trips = trips;
		}
	}

	public void setup() {
		size( width, height, P3D );
		
		map = loadImage("data/ui/mapbox.light.world.png");
		mercatorMap = new MercatorMap(width, height, lat2, lat1, lon1, lon2);
		
		for (int year = startYear; year <= endYear; year++) {
			table = loadTable("data/lattes-flows-country-"+year+".csv", "header");
			List<Flow> flowsByYear = new ArrayList<Flow>();
			float x,y;
			for (TableRow row : table.rows()) {
				Flow flow = new Flow(
					row.getFloat("oX"),
					row.getFloat("oY"),
					row.getFloat("dX"),
					row.getFloat("dY"),
					row.getInt("trips")
				);
				if(row.getInt("trips") > maxTrip)
					maxTrip = row.getInt("trips");
				flowsByYear.add(flow);
			}
			flows.put(year, flowsByYear);
		}
		
	}

	public void draw() {
		image(map, 0 ,0);
		
		textSize(20);
		fill(0, 102, 153, 204);
		String content = "Year "+currentYear;
		text(content, width/2-textWidth(content)/2, 45, 30);
		
		for(Flow f: flows.get(currentYear)){
			sizeFlows = flows.get(currentYear).size();
			myCurve(f);
			if(currentYear == endYear)
				break;
		}
	}

	void myCurve(Flow flow) {
//		float percentageTrip = flow.trips/(float)maxTrip;
//		int alfa = (int)((255/2)+(255*percentageTrip/2));
		stroke(255,0,0,200);
		strokeWeight(1);
		if (increment == 1){
			resolution = sizeFlows*50;
		}
		int step = (int) (resolution*0.8);
		if (increment<=resolution+step){
			increment++;
			int endValue = (increment > resolution)? resolution : increment;
			int startValue = (increment-step)<0 ? 0 : increment-step; 

			//curve
			for (int i=startValue; i<endValue;i++){ //0..endValue
				float t1 = i / (float)resolution;
				float t2 = (i+1)/ (float)resolution;
				//640,640 140,40 x 2 / 40,40x4 / 140,40x4
				float x1 = curvePoint(flow.begin.x, flow.begin.x, flow.end.x, flow.end.x, t1); 
				float y1 = curvePoint(flow.begin.y, flow.begin.y, flow.end.y, flow.end.y, t1);
				float x2 = curvePoint(flow.begin.x, flow.begin.x, flow.end.x, flow.end.x, t2);
				float y2 = curvePoint(flow.begin.y, flow.begin.y, flow.end.y, flow.end.y, t2);
				//int size = (int) (3+1-3*(i/(float)endValue)); 
				//ellipse(x1, y1, size, size);
				//ellipse(x2, y2, size, size);
				line(x1, y1, x2, y2);
			}
		}
		if (increment == resolution+step){
			currentYear++;
			increment = 0;
		}
//		println(year+" "+size+" "+increment+" "+resolution);
	}
}