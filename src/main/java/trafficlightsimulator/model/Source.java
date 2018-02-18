package trafficlightsimulator.model;

public class Source implements Agent{
	private Road nextRoad;
	private int generationDelay = (int) Math.max( (int)((Math.random()*100)%MP.maxEntry), MP.minEntry); //min of 2 for now 2 is more frequent creation // 
	
	public Car newCar = null;

	public Source(Road nextRoad) {
		this.nextRoad = nextRoad;
	}
	public Road getRoad(){
		return nextRoad;
	}
	private void setCar(Car newC){
		this.newCar = newC;
	}
	public void run(double time){
		Car newC = new Car(nextRoad);
		int numCars = nextRoad.getCars().size();
		
//		System.out.println(time);
		if ( ((int)(time*MP.timeStep*100)) % generationDelay == 0){
			if ( numCars <= 0) {
				nextRoad.getCars().add(newC);
				 setCar(newC);
				return;
			}else {
				Car nextCar = nextRoad.getCars().get(numCars-1); //grab last car
	
				if ( ((int)time  )%generationDelay == 0
					   && (nextCar.getPosition()-.5*nextCar.getLength()) > (.5*newC.getLength()) ){ //if there is not enough room for new car
	
					nextRoad.getCars().add(newC);
					
					setCar(newC);
					return;
				}
				setCar(null);
				return;
			}
		}
		else{
			setCar(null);
			return;
		}
	}
	
}
