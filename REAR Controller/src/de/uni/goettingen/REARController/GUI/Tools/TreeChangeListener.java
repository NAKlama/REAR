package de.uni.goettingen.REARController.GUI.Tools;

import java.util.EventListener;

public abstract interface TreeChangeListener extends EventListener {
	public void changedTreeStructure();
}
