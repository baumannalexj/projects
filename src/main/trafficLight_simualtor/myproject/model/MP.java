package myproject.model;

/**
 * Static class for model parameters.
 */
public class MP {
	
	private MP() {}
	
	/** Length of cars, in meters */
	public static double carLength = 10;
	/** Length of roads, in meters */
	public static double roadLength = 200;
	/** Maximum car velocity, in meters/second */
	public static double maxVelocity = 6;
	/** Width of Light */
	public static double lightWidth = 10;
	

	
	
	public static int 		numRows = 3; 
	public static int 		numCols = 3;
	public static double 	timeStep = 0.1;
	public static double	duration = 500; 
	public static boolean 	alternating = true;
	public static int 		minEntry = 2;
	public static int 		maxEntry = 25;
	public static double 	minRoad  = 200;
	public static double	maxRoad  = 500;
	public static double 	minCarLength  = 5;
	public static double 	maxCarLength  = 10;
	public static double 	minCarVelocity  = 10;
	public static double 	maxCarVelocity  = 30;
	public static double 	stopDistMin  = 0.5;
	public static double 	stopDistMax  = 5;
	public static double 	brakeDistMin  = 9;
	public static double 	brakeDistMax  = 10;
	public static double 	minGreen  = 30;
	public static double 	maxGreen  = 180;
	public static double 	minYellow  = 4;
	public static double 	maxYellow  = 5;

}

