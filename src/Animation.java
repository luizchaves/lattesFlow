
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

	int endYear = 2013;//2013
	int startYear = 2000;//1990, 1960, 1980
	int currentYear = startYear;
	int resolution = 0;
	int sizeFlows = 0;
	int increment=0;
	int maxTripSame = 0;
	int maxTripDiff = 0;
	int maxDist = 0;
	int flag = 0;
	
	boolean animating = true;
	
	class Flow{
		public PVector begin;
		public PVector end;
		public int trips;
		
		public Flow(float beginX, float beginY, float endX, float endY, int trips) {
			this.begin = mercatorMap.getScreenLocation(new PVector(beginX, beginY));
			this.end = mercatorMap.getScreenLocation(new PVector(endX, endY));
			this.trips = trips;
		}
		
		public boolean equalPoint(){
			return (begin.x==end.x && begin.y==end.y)? true :false;
		}
	}

	public void setup() {
		size( width, height, P3D );
		
		map = loadImage("data/ui/mapbox.light.world.1.png");
		mercatorMap = new MercatorMap(width, height, lat2, lat1, lon1, lon2);
		
		for (int year = startYear; year <= endYear; year++) {
			table = loadTable("data/flows/lattes-flows-country-"+year+".csv", "header");
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
				if(!flow.equalPoint() && (row.getInt("trips") > maxTripDiff)){
					maxTripDiff = row.getInt("trips");
				if(flow.equalPoint() && row.getInt("trips") > maxTripSame)
					maxTripSame = row.getInt("trips");
				}
				if(maxDist<dist(flow.begin.x, flow.begin.y, flow.end.x, flow.end.y))
					maxDist = (int)dist(flow.begin.x, flow.begin.y, flow.end.x, flow.end.y);
				flowsByYear.add(flow);
			}
			flows.put(year, flowsByYear);
		}
//		println(maxTripSame+" "+maxTripDiff+" "+maxDist);
	}

	public void draw() {
		background(209,209,209);
		
		if(currentYear <= endYear){
			textSize(20);
			fill(0, 102, 153, 204);
			String content = "Year "+currentYear;
			if(currentYear == endYear)
				content = "";
			text(content, width/2-textWidth(content)/2, 45, 30);
		}
		
		scale((float) 0.75);
		translate(100, 250);
		rotateX(radians(40));
		
		image(map, 0 ,0);
		
		flag++;
		if(animating){
			for(Flow f: flows.get(currentYear)){
				sizeFlows = flows.get(currentYear).size();
				myCurve(f);
			}
			//if(flag%3 == 0)
				//saveFrame("data/frames/######.png");
		}
	}

	void myCurve(Flow flow) {
//		float percentageTrip = flow.trips/(float)maxTripDiff;
//		int alfa = (int)((255*2/10)+(((255*8)/10)*percentageTrip));
//		int strokeSize = (int)((3*2/10)+(((3*8)/10)*percentageTrip));
//		stroke(255,0,0,alfa);
//		strokeWeight(strokeSize+1);
		
		stroke(255,0,0,30);
		strokeWeight(1);
		
		if (increment == 1){
			delay(1000);
			resolution = sizeFlows*50;//25,30,50
		}
		int step = (int) (resolution*2);//3, 2, 0.8
		if (increment<=resolution+step){
			increment++;
			int endValue = (increment > resolution)? resolution : increment;
			int startValue = (increment-step)<0 ? 0 : increment-step; 

			for (int i=startValue; i<endValue;i++){ //0..endValue
				float t1 = i / (float)resolution;
				float t2 = (i+1)/ (float)resolution;
				
				float percentageDist = dist(flow.begin.x, flow.begin.y, flow.end.x, flow.end.y)/(float)maxDist;
				percentageDist = (float) Math.pow(percentageDist, 2);
				int dist = (int)((5000/20)+(((5000*19)/10)*percentageDist));
				int direction = ((flow.begin.x > flow.end.x) && (flow.begin.y < flow.end.y)) ||
									((flow.begin.x < flow.end.x) && (flow.begin.y > flow.end.y)) ?
									+1/3 : -1/3;
				int offsetBegin = dist;
				int offsetEnd = dist;
				int offsetXBegin = 500*direction;
				int offsetYBegin = 500*direction;
				float x1 = curvePoint(flow.begin.x+offsetXBegin, flow.begin.x, flow.end.x, flow.end.x+offsetYBegin, t1); 
				float y1 = curvePoint(flow.begin.y+offsetBegin, flow.begin.y, flow.end.y, flow.end.y+offsetEnd, t1);
				float x2 = curvePoint(flow.begin.x+offsetXBegin, flow.begin.x, flow.end.x, flow.end.x+offsetYBegin, t2);
				float y2 = curvePoint(flow.begin.y+offsetBegin, flow.begin.y, flow.end.y, flow.end.y+offsetEnd, t2);
				
				//-640,0,0,-640 -140,0,0,-40 x 2 / -40,0,0,-40x4 / -140,0,0,-40x4 / -140,0,0,-140x4
				//int direction = (flow.begin.x > flow.end.x) ? -1 : 1;
				//int offsetBegin = 640*direction;
				//int offsetEnd = 640*direction;
				//float x1 = curvePoint(flow.begin.x+offsetBegin, flow.begin.x, flow.end.x, flow.end.x+offsetEnd, t1); 
				//float y1 = curvePoint(flow.begin.y+offsetBegin, flow.begin.y, flow.end.y, flow.end.y+offsetEnd, t1);
				//float x2 = curvePoint(flow.begin.x+offsetBegin, flow.begin.x, flow.end.x, flow.end.x+offsetEnd, t2);
				//float y2 = curvePoint(flow.begin.y+offsetBegin, flow.begin.y, flow.end.y, flow.end.y+offsetEnd, t2);

				//int size = (int) (3+1-3*(i/(float)endValue)); 
				//ellipse(x1, y1, size, size);
				//ellipse(x2, y2, size, size);
				
				line(x1, y1, x2, y2);
			}
			if (increment<=resolution){
			    float t1 = increment / (float)resolution;
			    float x1 = curvePoint(flow.begin.x-30, flow.begin.x, flow.end.x, flow.end.x-30, t1);
			    float y1 = curvePoint(flow.begin.y-30, flow.begin.y, flow.end.y, flow.end.y-30, t1);
			    ellipse(x1, y1, 5, 5); 
			}
		}
		if (increment == resolution+step){
			if(currentYear == endYear){
				animating = false;
			}
			if(currentYear != endYear){
				currentYear++;
			}
			increment = 0;
		}
//		println(year+" "+size+" "+increment+" "+resolution);
	}
}