package trafficlightsimulator.main;

import trafficlightsimulator.model.AnimatorBuilder;
import trafficlightsimulator.model.ModelParameters;
import trafficlightsimulator.model.Model;
import trafficlightsimulator.model.swing.SwingAnimatorBuilder;
import trafficlightsimulator.ui.UIError;
import trafficlightsimulator.ui.UIForm;
import trafficlightsimulator.ui.UIFormBuilder;
import trafficlightsimulator.ui.UIFormTest;
import trafficlightsimulator.ui.UIMenu;
import trafficlightsimulator.ui.UIMenuBuilder;
import trafficlightsimulator.ui.UI;

public class Control {
	private static final int EXITED = 0;
	private static final int EXIT = 1;
	private static final int START = 2;
	private static final int NUMSTATES = 10; //TODO this can change
	private UIMenu[] menus;
	private int state;

	private UIForm getRoadDimensionForm;
	private UIFormTest numberTest;
	private UIFormTest doubleTest;
	private UIFormTest stringTest;
	private UIFormTest dimensionTest;


	private Model model;
	private UI ui;
	private AnimatorBuilder builder;

	///model parameters // with defaults
	public int 		numRows = 3; 
	public int 		numCols = 3;
	public double 	timeStep = 0.5;
	public double 		duration = 500; 
	public boolean 	alternating = true;
	public int 		minEntry = 2;
	public int 		maxEntry = 25;
	public double 	minRoad  = 200;
	public double	maxRoad  = 500;
	public double 	minCarLength  = 5;
	public double 	maxCarLength  = 10;
	public double 	minCarVelocity  = 10;
	public double 	maxCarVelocity  = 30;
	public double 	stopDistMin  = 0.5;
	public double 	stopDistMax  = 5;
	public double 	brakeDistMin  = 9;
	public double 	brakeDistMax  = 10;
	public double 	minGreen  = 30;
	public double 	maxGreen  = 180;
	public double 	minYellow  = 4;
	public double 	maxYellow  = 5;


	Control(AnimatorBuilder builder, UI ui) {
		this.builder = builder;
		//		this.model = model;
		this.ui = ui;

		menus = new UIMenu[NUMSTATES];
		state = START;
		addSTART(START);
		addEXIT(EXIT);

		dimensionTest = input -> { //TODO restrict size to 5x5 for now
			try {
				int i = Integer.parseInt(input);
				return (i <=  5 && i > 0);
			} catch (NumberFormatException e) {
				return false;
			}
		};
		numberTest = input -> {
			try {
				Integer.parseInt(input) ;
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		};

		doubleTest = input -> {
			try {
				Double.parseDouble(input) ;
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		};
		stringTest = input -> ! "".equals(input.trim());

		UIFormBuilder f = new UIFormBuilder();
		f.add("Number of Horizontal Roads: (<= 0--> default", dimensionTest);
		f.add("Number of Vertical Roads (<= 0--> default):", dimensionTest);
		f.add("Time Step (<= 0--> default):", doubleTest);
		f.add("Runtime in Seconds (<= 0--> default):", doubleTest);
		f.add("Alternating Roads? 0 is no, any other number is yes", numberTest);
		f.add("Min Car entry delay: ( <= 0--> default)", numberTest); //minentry
		f.add("max Car entry delay:( <= 0--> default)", numberTest); //maxEntry 
		f.add("Min road length:( <= 0--> default)", doubleTest); //minRoad  
		f.add("max road length:( <= 0--> default)", doubleTest); //maxRoad  
		f.add("Min Car length:( <= 0--> default)", doubleTest); //minCarLength  
		f.add("max car length:( <= 0--> default)", doubleTest); //maxCarLength  
		f.add("Min car velocity:( <= 0--> default)", doubleTest); //minCarVelocity  
		f.add("max car velocity:( <= 0--> default)", doubleTest); //maxCarVelocity  
		f.add("min stop dist:( <= 0--> default)", doubleTest); //stopDistMin  
		f.add("max stop dist:( <= 0--> default)", doubleTest); //stopDistMax  
		f.add("Min brake dist:( <= 0--> default)", doubleTest); //brakeDistMin  
		f.add("max brake dist:( <= 0--> default)", doubleTest); //brakeDistMax  
		f.add("Min green time:( <= 0--> default)", doubleTest); //minGreen  
		f.add("max green time:( <= 0--> default)", doubleTest); //maxGreen  
		f.add("min yellow time:( <= 0--> default)", doubleTest); //minYellow  
		f.add("max yellow time:( <= 0--> default)", doubleTest); //maxYellow  

		getRoadDimensionForm = f.toUIForm("Enter Run Parameters");
	}

	void run() {
		try {
			while (state != EXITED) {
				ui.processMenu(menus[state]);
			}
		} catch (UIError e) {
			ui.displayError("UI closed");
		}
	}

	private void addSTART(int stateNum) {
		UIMenuBuilder m = new UIMenuBuilder();

		m.add("Default",
				() -> ui.displayError("Please Enter the Number From the Menu!"));
		m.add("Run",
				() -> {
					System.out.println("New Model Started");
					AnimatorBuilder builder = new SwingAnimatorBuilder() ;
//					model = new Model(builder, numRows, numCols);
					model = new Model(builder);
					System.out.println(" Simulation time step (seconds)       ["+timeStep+"]");
					System.out.println(" Simulation run time (seconds)        ["+ duration + "]");
					System.out.println(" Grid size (number of roads)          [row="+numRows+",column="+numCols+"]");
					System.out.println(" Traffic pattern                      [alternating: "+alternating+"]");
					System.out.println(" Car entry rate (seconds/car)         [min="+minEntry+",max="+maxEntry+"]");
					System.out.println(" Road segment length (meters)         [min="+minRoad+",max="+maxRoad+"]");
					System.out.println(" Intersection length (meters)         [min=10.0,max=15.0]");
					System.out.println(" Car length (meters)                  [min="+minCarLength+",max="+maxCarLength+"]");
					System.out.println(" Car maximum velocity (meters/second) [min="+minCarVelocity+",max="+maxCarVelocity+"]");
					System.out.println(" Car stop distance (meters)           [min="+stopDistMin+",max="+stopDistMax+"]");
					System.out.println(" Car brake distance (meters)          [min="+brakeDistMin+",max="+brakeDistMax+"]");
					System.out.println(" Traffic light green time (seconds)   [min="+minGreen+",max="+maxGreen+"]");
					System.out.println(" Traffic light yellow time (seconds)  [min="+minYellow+",max="+maxYellow+"]");


					model.run(duration);
					model.dispose();
					model = null;
					
				});
		m.add("Change Simulation Paraments",
				() -> {

					String[] result1 = ui.processForm(getRoadDimensionForm);
					//					Video v = Data.newVideo(result1[0], Integer.parseInt(result1[1]), result1[2]);
					ModelParameters.numRows  =( Integer.parseInt(result1[0]) <= 0)?   numRows  :  Integer.parseInt(result1[0]);
					ModelParameters.numCols  =( Integer.parseInt(result1[1]) <= 0)?   numCols  :  Integer.parseInt(result1[1]);
					ModelParameters.timeStep  =( Double.parseDouble(result1[2]) <= 0)?   timeStep  :  Double.parseDouble(result1[2]);
					ModelParameters.duration  = ( Double.parseDouble(result1[3]) <= 0)?   duration  :  Double.parseDouble(result1[3]);
					ModelParameters.alternating  = ( Integer.parseInt(result1[4]) == 0)?   false  :  true;
					ModelParameters.minEntry  =( Integer.parseInt(result1[5]) <= 0)?   minEntry  :  Integer.parseInt(result1[5]);
					ModelParameters.maxEntry  =( Integer.parseInt(result1[6]) <= 0)?   maxEntry  :  Integer.parseInt(result1[6]);
					ModelParameters.minRoad   =( Double.parseDouble(result1[7]) <= 0)?   minRoad   :  Double.parseDouble(result1[7]);
					ModelParameters.maxRoad   =( Double.parseDouble(result1[8]) <= 0)?   maxRoad   :  Double.parseDouble(result1[8]);
					ModelParameters.minCarLength   =( Double.parseDouble(result1[9]) <= 0)?   minCarLength   :  Double.parseDouble(result1[9]);
					ModelParameters.maxCarLength   =( Double.parseDouble(result1[10]) <= 0)?   maxCarLength   :  Double.parseDouble(result1[10]);
					ModelParameters.minCarVelocity   =( Double.parseDouble(result1[11]) <= 0)?   minCarVelocity   :  Double.parseDouble(result1[11]);
					ModelParameters.maxCarVelocity   =( Double.parseDouble(result1[12]) <= 0)?   maxCarVelocity   :  Double.parseDouble(result1[12]);
					ModelParameters.stopDistMin   =( Double.parseDouble(result1[13]) <= 0)?   stopDistMin   :  Double.parseDouble(result1[13]);
					ModelParameters.stopDistMax   =( Double.parseDouble(result1[14]) <= 0)?   stopDistMax   :  Double.parseDouble(result1[14]);
					ModelParameters.brakeDistMin   =( Double.parseDouble(result1[15]) <= 0)?   brakeDistMin   :  Double.parseDouble(result1[15]);
					ModelParameters.brakeDistMax   =( Double.parseDouble(result1[16]) <= 0)?   brakeDistMax   :  Double.parseDouble(result1[16]);
					ModelParameters.minGreen   =( Double.parseDouble(result1[17]) <= 0)?   minGreen   :  Double.parseDouble(result1[17]);
					ModelParameters.maxGreen   =( Double.parseDouble(result1[18]) <= 0)?   maxGreen   :  Double.parseDouble(result1[18]);
					ModelParameters.minYellow   =( Double.parseDouble(result1[19]) <= 0)?   minYellow   :  Double.parseDouble(result1[19]);
					ModelParameters.maxYellow   =( Double.parseDouble(result1[20]) <= 0)?   maxYellow   :  Double.parseDouble(result1[20]);




				});





		m.add("Exit",
				() -> state = EXIT);

		menus[stateNum] = m.toUIMenu("TRAFFIC SIMULATOR");
	}
	private void addEXIT(int stateNum) {
		UIMenuBuilder m = new UIMenuBuilder();
		m.add("Default", () -> {});
		m.add("Yes",
				() -> state = EXITED);
		m.add("No",
				() -> state = START);

		menus[stateNum] = m.toUIMenu("Are you sure you want to exit?");
	}
}
