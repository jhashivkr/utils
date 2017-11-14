package com.ibg.file.loader;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import com.ibg.db.ServiceLocator;
import com.ibg.utils.PropertyReader;
import com.ibg.utils.UploadVariables;

public class CollectFiles<T extends FileFilter> {
	protected static Scanner scanner = null;
	protected String USER_MSG = "\nEnter base / start file path: ";

	protected Set<File> jsonDmpfiles = null;
	protected DataFiles files = null;
	private FileFilter fileFilter = null;

	public CollectFiles() {
		scanner = new Scanner(System.in);
		jsonDmpfiles = new LinkedHashSet<File>();
		files = new DataFiles();
	}

	public void setFileFilter(T fileFilter) {
		this.fileFilter = fileFilter;
	}

	public Set<File> getAllFlatFile() {
		return files.getAllFlatFile();
	}

	public Set<File> getAllJsonFile() {
		jsonDmpfiles = files.getAllJsonFile();
		return files.getAllJsonFile();
	}

	protected void getAllDataFiles() {

		String startLocation = null;

		startLocation = ((PropertyReader) ServiceLocator.getBean("propertyReader")).getDataHome();
		if (null == startLocation) {
			System.out.print(USER_MSG);
			System.out.flush();
			startLocation = scanner.nextLine();
		}

		if (null != startLocation && (startLocation.trim().length() > 0)) {
			getAllDataFiles(startLocation);
			return;
		} else {
			System.out.println("Invalid Input ... Try Again");
			getAllDataFiles();
		}

	}

	protected void getAllDataFiles(String location) {

		File startLoc = new File(location);
		if (!startLoc.exists()) {
			System.out.println(startLoc + " - File / Directory doesn't exists");
			getAllDataFiles();
		}

		UploadVariables.setUploadStartLocation(startLoc);

		if (startLoc.isDirectory()) {
			// loop through all - this and child
			loopThruAndGetDataFiles(startLoc);

		} else if (startLoc.isFile()) {
			files.addFile(startLoc);

		}

		return;

	}

	protected void loopThruAndGetDataFiles(File location) {

		try {

			Set<File> listFiles = new LinkedHashSet<File>(Arrays.asList(location.listFiles()));
			for (File file : listFiles) {

				if (file.isFile()) {
					files.addFile(file);
				} else if (file.isDirectory()) {
					loopThruAndGetDataFiles(file);
				}

			}// for
		} catch (Exception ex) {
			System.out.println("Error: " + ex);
		}

		return;

	}

}
