import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Flow {
	MercatorMap mercatorMap;
	private PVector begin;
	private PVector end;
	private int trips;
	private PApplet pApplet;
	private static int MAX_TRIP_SAME;
	private static int MAX_TRIP_DIFF;
	private static int MAX_DIST;

	public Flow(float beginX, float beginY, float endX, float endY, int trips, MercatorMap mercatorMap, PApplet pApplet) {
		this.begin = mercatorMap.getScreenLocation(new PVector(beginX, beginY));
		this.end = mercatorMap.getScreenLocation(new PVector(endX, endY));
		this.trips = trips;
		this.pApplet = pApplet;

		if(!this.equalPoint() && (trips > MAX_TRIP_DIFF)){
			MAX_TRIP_DIFF = trips;
		}
		if(this.equalPoint() && trips > MAX_TRIP_SAME){
			MAX_TRIP_SAME = trips;
		}
		if(MAX_DIST<this.distance())
			MAX_DIST = (int)this.distance();
	}

	public boolean equalPoint(){
		return (begin.x==end.x && begin.y==end.y)? true :false;
	}

	public String idBegin(){
		return this.begin.x+","+this.begin.y;
	}
	
	public String idEnd(){
		return +this.end.x+","+this.end.y;
	}
	
	public String idBeginEnd(){
		return this.begin.x+","+this.begin.y+","+this.end.x+","+this.end.y;
	}

	public String idEndBegin(){
		return this.end.x+","+this.end.y+","+this.begin.x+","+this.begin.y;
	}

	public float distance(){
		return pApplet.dist(this.begin.x, this.begin.y, this.end.x, this.end.y);
	}

	public void drawPointsFlow(float instant, float size) {
		float x = pApplet.curvePoint(this.begin.x, this.begin.x, this.end.x, this.end.x, instant);
		float y = pApplet.curvePoint(this.begin.y, this.begin.y, this.end.y, this.end.y, instant);

		pApplet.pushMatrix();
		pApplet.stroke(0);
		pApplet.fill(0);
		pApplet.ellipse(x, y, size, size);
		pApplet.popMatrix();
	}

	public void drawCurve(int resolution) {
		boolean greatCircle = false;
		
		int endValue = resolution;
		float percentageDist = this.distance()/(float)MAX_DIST;
		percentageDist = (float) Math.pow(percentageDist, 2);
		int dist = (int)((5000*1/20)+(((5000*19)/20)*percentageDist));
		int direction = ((this.begin.x > this.end.x) && (this.begin.y < this.end.y)) ||
				((this.begin.x < this.end.x) && (this.begin.y > this.end.y)) ?
						+1/3 : -1/3;
		
		int offsetYBegin, offsetYEnd, offsetXBegin, offsetXEnd;
		
		if(!greatCircle){
			offsetYBegin = dist;
			offsetYEnd = dist;
			offsetXBegin = 500*direction;
			offsetXEnd = 500*direction;
		}else{
			direction = (this.begin.x > this.end.x) ? -1 : 1;
			offsetYBegin = offsetYEnd = offsetXBegin = offsetXEnd = -120*direction;//-140
		}

		for (int i=0; i<endValue;i++){
			float t1 = i /(float)resolution;
			float t2 = (i+1)/(float)resolution;

			float x1 = pApplet.curvePoint(offsetXBegin+this.begin.x, this.begin.x, this.end.x, offsetXEnd+this.end.x, t1); 
			float y1 = pApplet.curvePoint(offsetYBegin+this.begin.y, this.begin.y, this.end.y, offsetYEnd+this.end.y, t1);
			float x2 = pApplet.curvePoint(offsetXBegin+this.begin.x, this.begin.x, this.end.x, offsetXEnd+this.end.x, t2);
			float y2 = pApplet.curvePoint(offsetYBegin+this.begin.y, this.begin.y, this.end.y, offsetYEnd+this.end.y, t2);

			pApplet.pushMatrix();
			pApplet.stroke(255,0,0,30);
			pApplet.strokeWeight(1);
			pApplet.line(x1, y1, x2, y2);
			pApplet.popMatrix();
		}
	}

	public void drawCurveAnimated(int endValue, int startValue, int resolution) {
		boolean greatCircle = false;
		
		float percentageTrip = this.trips/(float)MAX_TRIP_DIFF;
		float percentageDist = this.distance()/(float)MAX_DIST;
		percentageDist = (float) Math.pow(percentageDist, 2);
		
		int dist = (int)((5000/20)+(((5000*19)/10)*percentageDist));
		int alfa = (int)((255*2/10)+(((255*8)/10)*percentageTrip));
		int strokeSize = (int)((3*2/10)+(((3*8)/10)*percentageTrip));
		
		int direction, offsetBegin, offsetEnd, offsetXBegin = 0, offsetYBegin = 0;
		if(!greatCircle){
			direction = ((this.begin.x > this.end.x) && (this.begin.y < this.end.y)) ||
					((this.begin.x < this.end.x) && (this.begin.y > this.end.y)) ?
							+1/3 : -1/3;
			//-640,0,0,-640 -140,0,0,-40 x 2
			offsetXBegin = 500*direction;
			offsetBegin = dist;
			offsetYBegin = 500*direction;
			offsetEnd = dist;
		}else{
			//-640,0,0,-640x4; -40,0,0,-40x4; -140,0,0,-40x4; -140,0,0,-140x4
			direction = (this.begin.x > this.end.x) ? -1 : 1;
			offsetBegin = 140*direction;
			offsetEnd = 140*direction;
		}
		
		for (int i=startValue; i<endValue;i++){ //0..endValue
			float x1, x2, y1, y2;
			float t1 = i / (float)resolution;
			float t2 = (i+1)/ (float)resolution;
			
			if(!greatCircle){
				x1 = pApplet.curvePoint(offsetXBegin+this.begin.x, this.begin.x, this.end.x, offsetYBegin+this.end.x, t1); 
				y1 = pApplet.curvePoint(offsetBegin+this.begin.y, this.begin.y, this.end.y, offsetEnd+this.end.y, t1);
				x2 = pApplet.curvePoint(offsetXBegin+this.begin.x, this.begin.x, this.end.x, offsetYBegin+this.end.x, t2);
				y2 = pApplet.curvePoint(offsetBegin+this.begin.y, this.begin.y, this.end.y, offsetEnd+this.end.y, t2);
			}else{	
				x1 = pApplet.curvePoint(offsetBegin+this.begin.x, this.begin.x, this.end.x, offsetEnd+this.end.x, t1); 
				y1 = pApplet.curvePoint(offsetBegin+this.begin.y, this.begin.y, this.end.y, offsetEnd+this.end.y, t1);
				x2 = pApplet.curvePoint(offsetBegin+this.begin.x, this.begin.x, this.end.x, offsetEnd+this.end.x, t2);
				y2 = pApplet.curvePoint(offsetBegin+this.begin.y, this.begin.y, this.end.y, offsetEnd+this.end.y, t2);
			}
			
			//int size = (int) (2+1-3*(i/(float)endValue)); 
			//pApplet.ellipse(x1, y1, size, size);
			//pApplet.ellipse(x2, y2, size, size);
			
			pApplet.pushMatrix();
			pApplet.stroke(255,0,0,alfa);
			pApplet.strokeWeight(strokeSize+1);
			pApplet.line(x1, y1, x2, y2);
			pApplet.popMatrix();
		}
	}

	public void drawBezier() {
		float percentageDist = this.distance()/(float)MAX_DIST;
		percentageDist = (float) Math.pow(percentageDist, 2);
		int dist = (int)((5000*1/20)+(((5000*19)/20)*percentageDist));
		
		pApplet.pushMatrix();
		pApplet.noFill();
		pApplet.stroke(255,0,0,50);
		pApplet.strokeWeight(1);
		pApplet.bezier(this.begin.x, this.begin.y, this.begin.x-20, this.begin.y-dist/10, this.end.x+20, this.end.y-dist/10, this.end.x, this.end.y);
		pApplet.popMatrix();
	}

	public void drawEllipse(int width, int height){
		float xo, yo, xe, ye;

		if(this.begin.x >= this.end.x){
			xo = this.begin.x;
			yo = this.begin.y;
			xe = this.end.x;
			ye = this.end.y;
		} else {
			xe = this.begin.x;
			ye = this.begin.y;
			xo = this.end.x;
			yo = this.end.y;
		}

		float xc = (xo+xe)/2;
		float yc = (yo+ye)/2;
		float dx = Math.abs(xo-xe);
		float dy = Math.abs(yo-ye);
		float diffXY = this.distance();;
		float diffY = Math.abs(yo-ye);
		float diffXOC = Math.abs(xo-xc);
		float diffYOC = Math.abs(yo-yc);
		float plusY = diffXY+diffXOC;//10,100

		plusY = diffXY*3/4;
		//plusY = (diffXY*3/4)/2;
		//plusY = diffXOC;	

		float ao, bo, ae, be, diff;

		if(ye>yo){  
			diff = (1/(float)2)*dx*(dy/height);
			bo = plusY;
			be = diffY+plusY;
			ao = diffXOC-diff;
			ae = diffXOC+diff;
			xc += diff;
		} else {
			diff = (1/(float)2)*dx*(dy/height);
			bo = diffY+plusY;
			be = plusY;
			ao = diffXOC+diff;
			ae = diffXOC-diff;
			xc -= diff;
		}
		//ae = be;
		//ao = bo;

		//pushMatrix();
		//stroke(0);
		//fill(0);
		//strokeWeight(0.2);
		//strokeWeight(1);
		//ellipse(xc, yc, 1, 1);
		//ellipse(xo, yo, 4, 4);
		//ellipse(xe, ye, 4, 4);
		//popMatrix();

		float xMax=0;
		float xMin=height;
		float diffX = 0;
		ArrayList<float[]> points = new ArrayList<float[]>();
		float percentageSlant = ((width/2f)-xc)/(width/2f);
		int slant = 20;//20, 30,70,120, 150
		if(diffXOC<40)
			slant = 0;
		slant = (int)(slant*percentageSlant);

		//ellipse equation
		//x = a*cos(t)    
		//y = b*sin(t)
		int startDegree = 0;
		for (float i = startDegree; i < 90; i++) {
			float x1 = (float)(Math.cos(i*Math.PI/180)*ao+xc);
			float y1 = (float)(-Math.sin(i*Math.PI/180)*bo+yo);
			float xs1 = (float)(x1+Math.tan(slant*Math.PI/180)*y1);
			if(i == startDegree)
				diffX = (float)(Math.tan(slant*Math.PI/180)*y1);
			xs1=diffX+xs1;
			float x2 = (float)(Math.cos((i+1)*Math.PI/180)*ao+xc);
			float y2 = (float)(-Math.sin((i+1)*Math.PI/180)*bo+yo);
			float xs2 = (float)(x2+Math.tan(slant*Math.PI/180)*y2);
			xs2=diffX+xs2;
			//if(xs1>xMax){
			if(i == startDegree){
				xMax = xs1; 
			} 
			float[] point = {xs1,y1,xs2,y2};
			if(y1<=yo)
				points.add(point);
		}

		int stopDegree = 180;
		for (float i = 90; i <= stopDegree; i++) {
			float x1 = (float)(Math.cos(i*Math.PI/180)*ae+xc);
			float y1 = (float)(-Math.sin(i*Math.PI/180)*be+ye);
			float xs1 = (float)(x1+Math.tan(slant*Math.PI/180)*y1);
			//if(i == stopDegree)
			//diffX = (float)(Math.tan(radians(slant))*y1);
			xs1=diffX+xs1;
			float x2 = (float)(Math.cos((i+1)*Math.PI/180)*ae+xc);
			float y2 = (float)(-Math.sin((i+1)*Math.PI/180)*be+ye);
			float xs2 = (float)(x2+Math.tan(slant*Math.PI/180)*y2);
			xs2=diffX+xs2;
			//if(xs1<xMin){
			if(i == stopDegree-1){
				xMin = xs1; 
			}
			float[] point = {xs1,y1,xs2,y2};
			if(y1<=ye)
				points.add(point);
		}

		//https://github.com/heygrady/transform/wiki/Calculating-2d-Matrices
		//http://www.mathworks.com/help/images/performing-general-2-d-spatial-transformations.html
		float percentageScale = (xo-xe)/(xMax-xMin);
		float diffX2 = xo-points.get(0)[0]*percentageScale;
		for(float[] point: points){
			pApplet.pushMatrix();
			pApplet.strokeWeight(1);
			pApplet.stroke(0, 102, 153, 30);//(142,68,173,20), (52,73,94,20),(255,0,0,50), (0, 102, 0, 50), (186,148,110,20)
			pApplet.line(point[0]*percentageScale+diffX2,point[1],point[2]*percentageScale+diffX2,point[3]);
			pApplet.popMatrix();
		}
	}
	
	public void drawSlantedEllipse(int width, int height){
		//float ir = radians(i);
	    //float ir2 = radians(i+1);
	    //slanted, rotated
	    //x' = a*cos(t)*cos(theta) - b*sin(t)*sin(theta) 
	    //y' = a*cos(t)*sin(theta) + b*sin(t)*cos(theta)
		//float x1 = (float)(a*Math.cos(ir)*Math.cos(slantr)-b*Math.sin(ir)*Math.sin(slantr)+x);
		//float y1 = (float)(-(a*Math.cos(ir)*Math.sin(slantr)+b*Math.sin(ir)*Math.cos(slantr))+y);
		//float x2 = (float)(a*Math.cos(ir2)*Math.cos(slantr)-b*Math.sin(ir2)*Math.sin(slantr)+x);
		//float y2 = (float)(-(a*Math.cos(ir2)*Math.sin(slantr)+b*Math.sin(ir2)*Math.cos(slantr))+y);
	}
	
}