package de.uni.goettingen.REARController.GUI.Tools;

import javax.swing.JSpinner;

public class Step {
	private int			step;
	private int			min;
	private int			max;
	private JSpinner	spin;
	
	public Step(int min_in, int max_in) {
		step	= 0;
		min		= min_in;
		max		= max_in;
		spin	= null;
	}
	
	public Step(int min_in, int max_in, int step_in) {
		step	= step_in;
		min		= min_in;
		max		= max_in;
		spin	= null;
	}
	
	public void linkSpinner(JSpinner s) {
		spin = s;
	}
	
	public void set(int i) {
		step = i;
		if(step > max || step < min)
			step = 0;
		if(spin != null)
			spin.setValue(step);
	}

	public void inc() {
		set(step+1);
		if(step > max)
			step = 0;
	}
	
	public void dec() {
		set(step-1);
		if(step < min)
			step = 0;
	}

	
	public int get() {
		return step;
	}
}
