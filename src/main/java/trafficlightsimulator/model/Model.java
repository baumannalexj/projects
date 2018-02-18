package trafficlightsimulator.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Observable;

import trafficlightsimulator.util.Animator;

/**
 * An example to model for a simple visualization.
 * The model contains roads organized in a matrix.
 * See {@link #Model(AnimatorBuilder, int, int)}.
 */
public class Model extends Observable {
	private List<Agent> 	agents;
	private Animator 		animator;
	private boolean 		disposed;
//	private double 			time =0;
	private List<Source> 	sources;


	/** Creates a model to be visualized using the <code>builder</code>.
	 *  If the builder is null, no visualization is performed.
	 *  The number of <code>rows</code> and <code>columns</code>
	 *  indicate the number of {@link Light}s, organized as a 2D
	 *  matrix.  These are separated and surrounded by horizontal and
	 *  vertical {@link Road}s.  For example, calling the constructor with 1
	 *  row and 2 columns generates a model of the form:
	 *  <pre>
	 *     |  |
	 *   --@--@--
	 *     |  |
	 *  </pre>
	 *  where <code>@</code> is a {@link Light}, <code>|</code> is a
	 *  vertical {@link Road} and <code>--</code> is a horizontal {@link Road}.
	 *  Each road has one {@link Car}.
	 *
	 *  <p>
	 *  The {@link AnimatorBuilder} is used to set up an {@link
	 *  Animator}.
	 *  {@link AnimatorBuilder#getAnimator()} is registered as
	 *  an observer of this model.
	 *  <p>
	 */
	public Model(AnimatorBuilder builder) {
		
		int rows = ModelParameters.numRows;
		int columns = ModelParameters.numCols;
		if (rows < 0 || columns < 0 || (rows == 0 && columns == 0)) {
			throw new IllegalArgumentException();
		}
		if (builder == null) {
			builder = new NullAnimatorBuilder();
		}
		this.agents = new ArrayList<Agent>();
		setup(builder, rows, columns);
		this.animator = builder.getAnimator();
		super.addObserver(animator);
	}

	/**
	 * Run the simulation for <code>duration</code> model seconds.
	 */
	public void run(double duration) {
		
		if (disposed)
			throw new IllegalStateException();
		
		double stepSize = ModelParameters.timeStep;
		for (double time= 0; time<duration; time+= stepSize) {
//			time++;
			
			// iterate through a copy because agents may change during iteration...
//			System.out.println(time);
			Agent newCar = null;
			for (Source s : sources) {
				//				System.out.println(s.getRoad());
				s.run(time);
				newCar = s.newCar; 
				if( newCar != null) agents.add(newCar);
			}
			for (Agent a : agents.toArray(new Agent[0])) {
				//TODO go through another list with cars who need to die 

				if ( (a instanceof Car) && ((Car)a).getRoad()== null){
//					System.out.println( "car killed: " + ((Car)a).getColor() );
					agents.remove(a);
				}
				else 
					a.run(time);

			}
			super.setChanged();
			super.notifyObservers();
		}
	}

	/**
	 * Throw away this model.
	 */
	public void dispose() {
		animator.dispose();
		disposed = true;
	}

	/**
	 * Construct the model, establishing correspondences with the visualizer.
	 */
	private void setup(AnimatorBuilder builder, int rows, int columns) {
		List<Road> 		roads = new ArrayList<Road>();
		Light[][] 		intersections = new Light[rows][columns];
		sources = new ArrayList<Source>();

		
		
		makeLights				(rows, columns, builder, intersections);
		
		boolean alternating 	= ModelParameters.alternating;
		makeVerticalStreets 	(rows, columns, builder, intersections, sources, roads, alternating);
		makeHorizontalStreets	(rows, columns, builder, intersections, sources, roads, alternating);

		}

	
	// make vertical roads method
	private void makeVerticalStreets(int rows, int columns, AnimatorBuilder builder, Light[][] intersections,
			List<Source> sources, List<Road> roads, boolean alternating) {

		//+++++++++++++ Add Vertical Roads
		boolean southToNorth = false;
		Road prevRoad = null;
		Light nextLight = null;
		final  boolean horizontal = true;
		Road l = null;
		
		

		for (int j=0; j<columns; j++) {
			for (int i=0; i<=rows; i++) {
				
				if (southToNorth){
					// ^ -P- # ^-L-+    (top - down == left to right)
					
					if (i>0) nextLight = intersections[i-1][j];
					l = new Road(nextLight, !horizontal); // light is null for first
					l.setNextRoad(prevRoad);
					if (i>=rows) sources.add(new Source(l));
				}
				else if(!southToNorth){ 
					// + -P-v # -L-v

					if (i<rows) nextLight = intersections[i][j];
					l = new Road( nextLight, !horizontal ); 
					if(prevRoad != null) prevRoad.setNextRoad(l);
					if ( i<=0) sources.add(new Source(l)); // +-l-v
				} 
		
				////      ----before next row ------
				builder.addVerticalRoad(l, i, j, southToNorth);
				roads.add(l);
				prevRoad = l;
				nextLight = null;
			}
			System.out.println();
			nextLight = null;
			prevRoad = null;
			if(alternating) southToNorth = !southToNorth;
		}
		
	}
	// make horizontal roads method
	private void makeHorizontalStreets(int rows, int columns, AnimatorBuilder builder, Light[][] intersections,
			List<Source> sources, List<Road> roads, boolean alternating ) {
		
		boolean eastToWest = false;
		final  boolean horizontal = true;
		Light nextLight = null;

		Road prevRoad = null;
		Road l= null;

		

		for (int i=0; i<rows; i++) { 
			for (int j=0; j<=columns; j++) {

				//new cleaner version eastToWest

				if(eastToWest){ //,<-p- # <-l-- +
					if (j>0 ) 	nextLight = intersections[i][j-1];
					l = new Road( nextLight, horizontal ); //nextLight is null for first one
					l.setNextRoad(prevRoad);
					if (j >= columns) sources.add(new Source(l)); // <--+
				}
				else if (!eastToWest){ // +--p-> # --l->
					if (j < columns) nextLight = intersections[i][j];
					l = new Road( nextLight, horizontal );
					if(prevRoad !=null) prevRoad.setNextRoad(l);
					if(j<=0) sources.add( new Source(l) ); // +-->				
				}
				
				////      -----before next column ------
				builder.addHorizontalRoad(l, i, j, eastToWest);
				roads.add(l);
				prevRoad = l;
				nextLight = null; 

			}
			System.out.println();
			nextLight = null;
			prevRoad = null;
			if (alternating)  eastToWest = !eastToWest;
		}
		
	}
	
	// make lights  method
	private void makeLights(int rows, int columns, AnimatorBuilder builder, Light[][] intersections) {
		for (int i=0; i<rows; i++) { 
			for (int j=0; j<columns; j++) {
				//Add Lights
				intersections[i][j] = new Light();
				builder.addLight(intersections[i][j], i, j);
				agents.add(intersections[i][j]);
			}
		}
	}
	



}
