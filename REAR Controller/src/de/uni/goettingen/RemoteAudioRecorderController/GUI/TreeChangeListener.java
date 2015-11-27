package de.uni.goettingen.RemoteAudioRecorderController.GUI;

import java.util.EventListener;

public abstract interface TreeChangeListener extends EventListener {
	public void changedTreeStructure();
}
