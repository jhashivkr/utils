package com.ibg.file.loader;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ibg.db.ServiceLocator;
import com.ibg.utils.FileFilterUtil;
import com.ibg.utils.PropertyReader;

public class DoneFileCollector extends CollectFiles<FileFilter> {

	private Map<String, LoadFileDetails> doneFiles = new LinkedHashMap<String, LoadFileDetails>();

	public DoneFileCollector() {

	}

	public Map<String, LoadFileDetails> getDoneFiles() {
		getAllDoneFiles();
		return doneFiles;
	}

	protected void getAllDoneFiles() {

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
			getAllDoneFiles();
		}

	}

	protected void getAllDoneFiles(String location) {

		File startLoc = new File(location);
		if (!startLoc.exists()) {
			System.out.println(startLoc + " - File / Directory doesn't exists");
			getAllDoneFiles();
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
			System.out.println("Error from DoneFileCollector: " + ex);
		}

		return;

	}

	private void addFile(File file) {
		String grp = "";
		if (null != file) {
			if (FileFilterUtil.done.equalsIgnoreCase(FileFilterUtil.getExtension(file.getAbsolutePath()))) {
				grp = FileFilterUtil.getNameWithoutExtension(file);
				LoadFileDetails loadFileDetails = new LoadFileDetails();
				loadFileDetails.setProcessStat(false);
				loadFileDetails.setDataProcGrp(grp);
				doneFiles.put(grp, loadFileDetails);
			}
		}
	}

}
