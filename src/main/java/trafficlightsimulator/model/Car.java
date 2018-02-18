package trafficlightsimulator.model;

/**
 * A car remembers its position from the beginning of its road.
 * Cars have random velocity and random movement pattern:
 * when reaching the end of a road, the dot either resets its position
 * to the beginning of the road, or reverses its direction.
 */
public class Car implements Agent {
	private Road currentRoad;
	private double length = Math.max( Math.round(Math.random()*100)%MP.maxCarLength, MP.minCarLength)   ;//MP.carLength; MP.carLength will be the max car length
	private java.awt.Color color = new java.awt.Color((int)Math.ceil(Math.random()*255),(int)Math.ceil(Math.random()*255),(int)Math.ceil(Math.random()*255));

	Car( Road road) {this.currentRoad = road; } // Created only by this package

	private Car nextCar = null;
	private int thisCarIndex;

	private int  idleSpeed = 1;

	private double position = 0;
	//	private double velocity = idleSpeed + (int) Math.ceil(Math.random() * MP.maxVelocity); // this was velocity first

	private double maxVelocity = Math.max( ((Math.random()*100)%MP.maxCarVelocity), MP.minCarVelocity);//idleSpeed + (int) Math.ceil(Math.random() * MP.maxVelocity); // this was velocity first
	private double currentVelocity = 1;
	private double acceleration =  0.1;

	private double brakeDistance = MP.brakeDistMin;
	private double stopDistance = MP.brakeDistMax;

	public double getPaintPosition(){
		return position*MP.roadLength/this.getRoad().length;
	}
	public double getLength(){
		return length;
	}
	public Road getRoad(){
		return currentRoad;
	}
	private void changeRoad(Road newRoad){
		newRoad.accept(this);
		currentRoad.getCars().remove(this);
		currentRoad = newRoad;
	}
	private void killCar(){
		position += currentVelocity ; //move one last time (more fluid)
		currentRoad.getCars().remove(this); 
		this.currentRoad = null;
		return;
	}
	
	public double getPosition() {
		return position;
	}
	public java.awt.Color getColor() {
		return color;
	}

//	Car getNextCar(Road roadCursor){
//		int thisCarIndex = currentRoad.getCars().indexOf(this);
//
//		if (thisCarIndex >0 ){ // if not first, then grab next car
//			return currentRoad.getCars().get(thisCarIndex - 1) ; // then grab one car infront;
//		}else{
//			return getNextCarHelper(roadCursor);
//		}
//
//	}
//	Car getNextCarHelper(Road roadCursor){
//		if( roadCursor == null) 
//			return null;
//		if( !roadCursor.getCars().isEmpty()){
//			int numCars = roadCursor.getCars().size();
//			return roadCursor.getCars().get(numCars-1); // get last car
//		}
//		return getNextCar(roadCursor.getNextRoad());
//	}



	//------------------------------------------------------------
	public void run(double time) {
		double intersectionGap = 2*1 + MP.carLength +.5*length; //TODO match with VP
		double elementWidth = 1;
		int thisCarIndex = currentRoad.getCars().indexOf(this);
		double endOfRoad = currentRoad.length - .5*length; //- gap ;// - 2*gap;

		//TODO CREATE A METHOD TO GET THE NEXT CAR, WHETHER ON THIS ROAD or NEXT

		//		if (nextCar == null)  nextCar = getNextCar(currentRoad); // if car in front was destroyed or doesn't exist check again


		//collision and light decisions

		Car nextCar;

		////dist to next car calc
		double  distToNextCar = 999*length; //  ==== |-L-x <-D-> |-L2-x ======
		if (thisCarIndex > 0){ // if not first car ==> next car is on this road so positions are comparable
			nextCar = currentRoad.getCars().get(thisCarIndex-1);
			distToNextCar = -currentVelocity + (nextCar.position - .5*nextCar.length) - (position + .5*length);
		}else {
			if (currentRoad.getNextRoad() == null) distToNextCar = 999*length;
			else{
				nextCar = currentRoad.getNextRoad().getLastCar();
				distToNextCar = (nextCar == null)
						? 2*MP.roadLength*length
						: -currentVelocity + (currentRoad.length+nextCar.position - .5*nextCar.length) - (position + .5*length);

			}
		}


		//collision and end of road and light
		if (distToNextCar <= stopDistance){
			currentVelocity = 0;
//			position = position;
			return;
		}else if (distToNextCar  <= brakeDistance){
			currentVelocity = Math.max(currentVelocity-acceleration, 0);
			position = Math.max(position, position + currentVelocity);
			return;
		}else {//check light or end of road
			//TODO CHANGEME should be currentRoad.horizontal
			
			
			if (currentRoad.nextLight!=null ){
				if ( (currentRoad.nextLight.getColor(currentRoad.horizontal).equals("yellow")
						||currentRoad.nextLight.getColor(!currentRoad.horizontal).equals("pink")
						)
						&& (currentRoad.length-position+.5*length <= brakeDistance)){
					currentVelocity = Math.max( 0,currentVelocity-acceleration);//slow down
				}
			}	
			if (position + currentVelocity + .5*length >= endOfRoad){
				Road nextRoad = currentRoad.getNextRoad();

				if(nextRoad == null){ 	//kill car
					killCar();
				}
				//check if light is yellow and within brake or stop distance)
				else if ( currentRoad.nextLight.getColor(currentRoad.horizontal).equals("red")
						|| currentRoad.nextLight.getColor(currentRoad.horizontal).equals("pink")){
					//light is red
					currentVelocity = 0;
					position = Math.min(position, endOfRoad);
					return;
				}else{ //transfer roads!
//					nextRoad.accept(this);
//					currentRoad.getCars().remove(this);
//					currentRoad = nextRoad;
					changeRoad(nextRoad);
					currentVelocity += acceleration;
					position = -intersectionGap; //TODO ha, gah it's 8* gap
					return;

				}

			}else{
				position += currentVelocity;
				currentVelocity += acceleration;
			}
		}



		//   +++++++ //default
		//					if ((position + velocity) > (MP.roadLength-this.length)){
		//						position = 0;
		//					}
		//				
		//				position += velocity;
	}
}
