
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

	int width = 1025;//1000,1025
	int height = 1000;//1000

	MercatorMap mercatorMap;

	Table table;
	Map<Integer, List<Flow>> flows = new Hashtable<Integer, List<Flow>>();
	Map<Integer, Map<String, Integer>> points = new Hashtable<Integer, Map<String, Integer>>();

	int endYear = 2013;//2013
	int startYear = 2000;//2000, 1990, 1960, 1980
	int currentYear = startYear;
	int resolution = 0;
	int sizeFlows = 0;
	int increment=0;
	int maxTripSame = 0;
	int maxTripDiff = 0;
	int maxDist = 0;
	int flag = 0;
	
	boolean rotateMap = true;
	boolean cumulatePath = true;
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

	class MyCurve{
		PVector source;
		PVector target;

		public MyCurve(PVector source, PVector target){
			this.source = source;
			this.target = target;
		}

		void show(){
			float xo, yo, xe, ye;

			if(this.source.x >= this.target.x){
				xo = this.source.x;
				yo = this.source.y;
				xe = this.target.x;
				ye = this.target.y;
			} else {
				xe = this.source.x;
				ye = this.source.y;
				xo = this.target.x;
				yo = this.target.y;
			}

			float xc = (xo+xe)/2;
			float yc = (yo+ye)/2;
			float dx = Math.abs(xo-xe);
			float dy = Math.abs(yo-ye);
			float diffXY = dist(this.source.x, this.source.y, this.target.x, this.target.y)/2;
			float diffY = Math.abs(yo-ye);
			float diffXOC = Math.abs(xo-xc);
			float diffYOC = Math.abs(yo-yc);
			float diffXEC = Math.abs(xe-xc);
			float diffYEC = Math.abs(ye-yc);
			float plusY = diffXY+diffXOC;//10,100
			
			plusY = diffXY*3/4;
//			plusY = diffXOC;	

			float ao, bo, ae, be, diff;

			if(ye>yo){  
				diff = (1/(float)2)*dx*(dy/height);
//				diff = 0;
				bo = plusY;
				be = diffY+plusY;
				ao = diffXOC-diff;
				ae = diffXOC+diff;
				xc += diff;
			} else {
				diff = (1/(float)2)*dx*(dy/height);
//				diff = 0;
				bo = diffY+plusY;
				be = plusY;
				ao = diffXOC+diff;
				ae = diffXOC-diff;
				xc -= diff;
			}
//			ae = be;
//			ao = bo;
			float xMax=0;
		    float xMin=height;
		    
//			stroke(0);
//			fill(0);
//			strokeWeight(0.2);
//			strokeWeight(1);
//		    ellipse(xc, yc, 1, 1);
//			ellipse(xo, yo, 4, 4);
//			ellipse(xe, ye, 4, 4);
			
			//ellipse equation
		    //x = a*cos(t)    
		    //y = b*sin(t)
		    
		    float diffX = 0;
		    ArrayList<float[]> points = new ArrayList<float[]>();
		    float percentageSlant = ((width/2f)-xc)/(width/2f);
		    int slant = 20;//20, 30,70,120, 150
		    slant = (int)(slant*percentageSlant);

		    int startDegree = 0;
			for (float i = startDegree; i < 90; i++) {
				float x1 = (float)(Math.cos(radians(i))*ao+xc);
				float y1 = (float)(-Math.sin(radians(i))*bo+yo);
				float xs1 = (float)(x1+Math.tan(radians(slant))*y1);
				if(i == startDegree)
					diffX = (float)(Math.tan(radians(slant))*y1);
				xs1=diffX+xs1;
				float x2 = (float)(Math.cos(radians(i+1))*ao+xc);
				float y2 = (float)(-Math.sin(radians(i+1))*bo+yo);
				float xs2 = (float)(x2+Math.tan(radians(slant))*y2);
				xs2=diffX+xs2;
//			    if(xs1>xMax){
				if(i == startDegree){
			        xMax = xs1; 
			    } 
			    float[] point = {xs1,y1,xs2,y2};
			    if(y1<=yo)
			    	points.add(point);
			}

			int stopDegree = 180;
			for (float i = 90; i <= stopDegree; i++) {
				float x1 = (float)(Math.cos(radians(i))*ae+xc);
				float y1 = (float)(-Math.sin(radians(i))*be+ye);
				float xs1 = (float)(x1+Math.tan(radians(slant))*y1);
//			    if(i == stopDegree)
//			        diffX = (float)(Math.tan(radians(slant))*y1);
			    xs1=diffX+xs1;
				float x2 = (float)(Math.cos(radians(i+1))*ae+xc);
				float y2 = (float)(-Math.sin(radians(i+1))*be+ye);
				float xs2 = (float)(x2+Math.tan(radians(slant))*y2);
				xs2=diffX+xs2;
//			    if(xs1<xMin){
			    if(i == stopDegree-1){
			        xMin = xs1; 
			    }
			    float[] point = {xs1,y1,xs2,y2};
			    if(y1<=ye)
			    	points.add(point);
			}
			
		    float percentageScale = (xo-xe)/(xMax-xMin);
		    float diffX2 = xo-points.get(0)[0]*percentageScale;
		    for(float[] point: points){
		    	//https://github.com/heygrady/transform/wiki/Calculating-2d-Matrices
		    	//http://www.mathworks.com/help/images/performing-general-2-d-spatial-transformations.html
		    	pushMatrix();
		    	strokeWeight(1);
//		    	stroke(255,0,0,50);//1,50
				stroke(0, 102, 0, 50);
		    	line(point[0]*percentageScale+diffX2,point[1],point[2]*percentageScale+diffX2,point[3]);
		    	popMatrix();
		    }
		}
	}

	public void setup() {
		size(width, height, P3D );

		map = loadImage("data/ui/mapbox.streets.world.3.png");
//		map = loadImage("data/ui/mapbox.light.world.1.png");
//		map = loadImage("data/ui/mapbox.world.2.png");
//		map = loadImage("data/ui/mapbox.comic.world.2.png");
		mercatorMap = new MercatorMap(width, height, lat2, lat1, lon1, lon2);

		for (int year = startYear; year <= endYear; year++) {
			table = loadTable("data/flows/lattes-flows-country-"+year+".csv", "header");
			List<Flow> flowsByYear = new ArrayList<Flow>();
			Map<String, Integer> pointsByYear = new Hashtable<String, Integer>();
			float x,y;
			for (TableRow row : table.rows()) {
				Flow flow = new Flow(
						row.getFloat("oX"),
						row.getFloat("oY"),
						row.getFloat("dX"),
						row.getFloat("dY"),
						row.getInt("trips")
						);
				int trips = row.getInt("trips");
				if(!flow.equalPoint() && (trips > maxTripDiff)){
					maxTripDiff = trips;
				}
				if(flow.equalPoint() && trips > maxTripSame){
					maxTripSame = trips;
				}
				if(maxDist<dist(flow.begin.x, flow.begin.y, flow.end.x, flow.end.y))
					maxDist = (int)dist(flow.begin.x, flow.begin.y, flow.end.x, flow.end.y);
				flowsByYear.add(flow);
				
				String key = flow.begin.x+","+flow.begin.y;
				if(pointsByYear.containsKey(key)){
					pointsByYear.put(key, pointsByYear.get(key)-trips);
				}else{
					pointsByYear.put(key, -trips);
				}
				
				key = flow.end.x+","+flow.end.y;
				if(pointsByYear.containsKey(key)){
					pointsByYear.put(key, pointsByYear.get(key)+trips);
				}else{
					pointsByYear.put(key, trips);
				}
			}
			flows.put(year, flowsByYear);
			points.put(year, pointsByYear);
		}
//		println(maxTripSame+" "+maxTripDiff+" "+maxDist);
	}

	public void draw() { 
//		background(209,209,209); //light.world
		background(241,239,241); //world, streets.world
//		background(0,71,109); //comic.world

		if(currentYear <= endYear && animating){
			pushMatrix();
			textSize(20);
			fill(0, 102, 153, 204);
			String content = "Year "+currentYear;
//			text(content, width/2-textWidth(content)/2, 45, 30);
			text(content, 45, 45, 30);
			popMatrix();
		}
		if(rotateMap){
			scale((float) 0.75);
//			translate(100, 250);
//			translate(100, 100);
			translate(130, 100);
			rotateX(radians(40));			
		}

		image(map, 0 ,0);

		flag++;
		if(animating){
			pushMatrix();
			translate(-32, 0);//mapbox.streets.world!!!
			myCurve();
//			myCurveStepByStep();
			popMatrix();
//			if(flag%3 == 0)
				saveFrame("data/frames/######.png");
		}
	}

	void myCurve() {
		Map<String, Integer> pointsExist = new Hashtable<String, Integer>();
		for(int year = startYear; year<=currentYear; year++){
			sizeFlows = 0;
			for(Flow flow: flows.get(year)){
				
				sizeFlows += flows.get(year).size();

				boolean exist = pointsExist.containsKey(flow.begin.x+","+flow.begin.y+","+flow.end.x+","+flow.end.y) ||
						pointsExist.containsKey(flow.end.x+","+flow.end.y+","+flow.begin.x+","+flow.begin.y);
				if(!exist)
					pointsExist.put(flow.begin.x+","+flow.begin.y+","+flow.end.x+","+flow.end.y, 1);
				if(!exist && cumulatePath){
					(new MyCurve(new PVector(flow.begin.x, flow.begin.y), new PVector(flow.end.x,flow.end.y))).show();
				}	
				if(year == currentYear && !cumulatePath){
					(new MyCurve(new PVector(flow.begin.x, flow.begin.y), new PVector(flow.end.x,flow.end.y))).show();
				}

				//curve
//				int endValue = resolution;
//				for (int i=0; i<endValue;i++){
//					float t1 = i / (float)resolution;
//					float t2 = (i+1)/ (float)resolution;
//
//					float percentageDist = dist(flow.begin.x, flow.begin.y, flow.end.x, flow.end.y)/(float)maxDist;
//					percentageDist = (float) Math.pow(percentageDist, 2);
//					int dist = (int)((5000*1/20)+(((5000*19)/20)*percentageDist));
//					int direction = ((flow.begin.x > flow.end.x) && (flow.begin.y < flow.end.y)) ||
//							((flow.begin.x < flow.end.x) && (flow.begin.y > flow.end.y)) ?
//									+1/3 : -1/3;
//					int offsetYBegin = dist;
//					int offsetYEnd = dist;
//					int offsetXBegin = 500*direction;
//					int offsetXEnd = 500*direction;
//					float x1 = curvePoint(flow.begin.x+offsetXBegin, flow.begin.x, flow.end.x, flow.end.x+offsetXEnd, t1); 
//					float y1 = curvePoint(flow.begin.y+offsetYBegin, flow.begin.y, flow.end.y, flow.end.y+offsetYEnd, t1);
//					float x2 = curvePoint(flow.begin.x+offsetXBegin, flow.begin.x, flow.end.x, flow.end.x+offsetXEnd, t2);
//					float y2 = curvePoint(flow.begin.y+offsetYBegin, flow.begin.y, flow.end.y, flow.end.y+offsetYEnd, t2);
//
//					line(x1, y1, x2, y2);
//				}
				//bezier(flow.begin.x, flow.begin.y, flow.begin.x-20, flow.begin.y-dist/10, flow.end.x+20, flow.end.y-dist/10, flow.end.x, flow.end.y);

				if(year == currentYear){
					if (increment == 1){
						resolution = sizeFlows*10;
					}
					if (increment<=resolution){
						increment++;
						
						float t1 = increment / (float)resolution;
						float x1 = curvePoint(flow.begin.x, flow.begin.x, flow.end.x, flow.end.x, t1);
						float y1 = curvePoint(flow.begin.y, flow.begin.y, flow.end.y, flow.end.y, t1);

//						if(t1>=0.1 && t1<=0.9){
							pushMatrix();
							stroke(0);
							fill(0);
							ellipse(x1, y1, 3, 3);
							popMatrix();
//						}
						
					}
					if (increment == resolution){
						if(currentYear == endYear){
							animating = false;
						}
						if(currentYear != endYear){
							currentYear++;
						}
						increment = 0;
					}
				}
				
			}
			for(String point: points.get(currentYear).keySet()){
				pushMatrix();
				if(points.get(currentYear).get(point)<0){
					stroke(255,0,0);
					fill(255,0,0);
				}else{
					stroke(0,0,255);
					fill(0,0,255);
				}
				ellipse(Float.parseFloat(point.split(",")[0]),Float.parseFloat(point.split(",")[1]),10,10);
				popMatrix();
			}
		}
	}

	void myCurveStepByStep() {
		for(Flow flow: flows.get(currentYear)){
			sizeFlows = flows.get(currentYear).size();

			float percentageTrip = flow.trips/(float)maxTripDiff;
			int alfa = (int)((255*2/10)+(((255*8)/10)*percentageTrip));
			int strokeSize = (int)((3*2/10)+(((3*8)/10)*percentageTrip));
			stroke(255,0,0,alfa);
			strokeWeight(strokeSize+1);

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
		}
	}
}