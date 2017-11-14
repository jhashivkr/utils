package com.ibg.file.loader;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ibg.db.ServiceLocator;
import com.ibg.utils.FileFilterUtil;
import com.ibg.utils.PropertyReader;

public class FileCollector extends CollectFiles<FileFilter> {

	private Map<String, LoadFileDetails> doneFiles;

	public FileCollector(Map<String, LoadFileDetails> doneFiles) {
		this.doneFiles = doneFiles;
	}

	public Map<String, LoadFileDetails> getDoneFiles() {
		getAllDataFiles();
		return doneFiles;
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
					addFile(file);
				} else if (file.isDirectory()) {
					loopThruAndGetDataFiles(file);
				}

			}// for
		} catch (Exception ex) {
			System.out.println("Error from FileCollector: " + ex);
		}

		return;

	}

	private void addFile(File file) {
		String grp = "";
		if (null != file) {
			if (FileFilterUtil.txt.equalsIgnoreCase(FileFilterUtil.getExtension(file.getAbsolutePath()))) {
				grp = FileFilterUtil.getBaseGrpName(file);
				if (doneFiles.containsKey(grp)) {
					doneFiles.get(grp).getFlatFileSet().add(file);
				}
			} else if (FileFilterUtil.json.equalsIgnoreCase(FileFilterUtil.getExtension(file.getAbsolutePath()))) {
				grp = FileFilterUtil.getBaseGrpName(file);
				if (doneFiles.containsKey(grp)) {
					doneFiles.get(grp).getJsonFileSet().add(file);
				}
			}
		}
	}

}
