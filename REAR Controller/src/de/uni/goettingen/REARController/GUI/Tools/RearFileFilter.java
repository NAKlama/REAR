package de.uni.goettingen.REARController.GUI.Tools;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class RearFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if(f.isDirectory())
			return false;
		
		String s = f.getName();
		return s.endsWith(".rear") || s.endsWith(".REAR");
	}

	@Override
	public String getDescription() {
		return "*.rear, *.REAR";
	}

}
