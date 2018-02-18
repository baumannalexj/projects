package trafficlightsimulator.model;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * A road holds cars.
 */
public class Road {

	final boolean horizontal;
	private Road nextRoad; //could be light or sink;	
	final Light nextLight;
	Random rand = new Random();
	final public double length =   Math.max(ModelParameters.minRoad, Math.round(Math.random()*100)% ModelParameters.maxRoad); //rand.nextDouble()*300 + 200 ;// + ModelParameters.roadLength; // need to mess with translator and swingTODO put this in constructor later to be between 200 -- 500
	
	Road(Light nextLight, boolean horizontal) { 
		this.nextLight = nextLight;
		this.horizontal = horizontal;
	} // Created only by this package
	
	void setNextRoad(Road r){
		nextRoad = r;
	}
	Road getNextRoad(){
		return nextRoad ;
	}
	
	Car getLastCar(){
		if (getCars().isEmpty()) return null;					
		return getCars().get(getCars().size()-1);
	}
	
	private List<Car> cars = new ArrayList<Car>();

	public void accept(Car d) {
		if (d == null) { throw new IllegalArgumentException(); }
		cars.add(d);
	}
	public List<Car> getCars() {
		return cars;
	}
}
