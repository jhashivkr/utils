package com.ibg.utils;

import java.io.File;
import java.io.FileFilter;

public class JsonFileFilter implements FileFilter {
	
	public boolean accept(File dir, String filename) {
		return filename.endsWith(".json");
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = FileFilterUtil.getExtension(f);
		if (extension != null) {
			if (extension.equals(FileFilterUtil.json)) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	// The description of this filter
	public String getDescription() {
		return "Json files only";
	}
}
