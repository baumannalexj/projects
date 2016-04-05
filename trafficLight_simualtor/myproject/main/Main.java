package myproject.main;

import myproject.ui.*;
//import myproject.model.Model;
import myproject.model.swing.SwingAnimatorBuilder;

/**
 * A static class to demonstrate the visualization aspect of
 * simulation.
 */
public class Main {
	private Main() {}
	public static void main(String[] args) {

		
		{
			// WITH UI
		UI ui = null;
//		ui = new TextUI();
		ui = new PopupUI();
		Control control = new Control(new SwingAnimatorBuilder(), ui); //TODO takes interface animatorbuilder, could get textanimator working too
		control.run();
		}
		
		
		{
			// MANUAL
//			int numRows = 3;
//			int numCols = 3;
//			Model m = new Model(new SwingAnimatorBuilder(), numRows,numCols); //2, 3);
//			m.run(500); // TODO get test UI to work
//			m.run(500);
//			
//			
//			m.run(500);
//			m.dispose();
		}
		System.exit(0);
	}
}

