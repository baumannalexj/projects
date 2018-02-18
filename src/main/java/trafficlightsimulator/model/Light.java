package trafficlightsimulator.model;

import java.util.Random;

/**
 * A light has a boolean state.
 */
public class Light implements Agent {
	private boolean green = false;
	Random rand = new Random();
	private int  greenTime = (int) Math.max(ModelParameters.minGreen,rand.nextInt((int) ModelParameters.maxGreen) );
	private int  yellowTime = (int) Math.max(ModelParameters.minYellow,rand.nextInt((int) ModelParameters.maxYellow) );
	private int counter = (int) (rand.nextInt(15))%4;
	Light() {} // Created only by this package
	
	public boolean getState() {
		return green;
	}
	public String getColor(boolean horizontal){
		int vert = 0;
		if (horizontal) {
			vert = 2; 
		}
		
		switch( (counter+vert)%4 ){
		case 0:
			return "green";
		case 1:
			return "yellow";
		case 2:
			return "red";
		case 3:
			return "pink";
		}
		
		return  "X";
	}
	
	public void run(double time) {
		if ( getColor(true).equals("green") ){
			if ( (int)(ModelParameters.timeStep * time * 100) % greenTime ==0 )				//(time%40==0) { //TODO how fast the light changes
				counter=(counter+1)%4;
		}
		else if  (getColor(true).equals("yellow") ){ 
			if ( (int)(ModelParameters.timeStep * time * 100) % yellowTime ==0 )			//(time%40==0) { //TODO how fast the light changes
				counter=(counter+1)%4;
		}
		else  { 
			if ( (int)(ModelParameters.timeStep * time * 100) % (greenTime + yellowTime) ==0 )			//(time%40==0) { //TODO how fast the light changes
				counter=(counter+1)%4;
		}
	}
}

