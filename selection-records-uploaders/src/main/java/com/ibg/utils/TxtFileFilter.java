package com.ibg.utils;

import java.io.File;
import java.io.FileFilter;

public class TxtFileFilter implements FileFilter {

	public boolean accept(File dir, String filename) {
		return filename.endsWith(".txt");
	}

	// Accept all directories and all txt files.
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = FileFilterUtil.getExtension(f);
		if (extension != null) {
			if (extension.equals(FileFilterUtil.txt)) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	// The description of this filter
	public String getDescription() {
		return ".txt only";
	}
}
