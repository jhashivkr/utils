package com.ibg.file.loader;

import java.io.File;
import java.io.FileFilter;

import com.ibg.utils.UploadVariables;

public class CollectErrorJsonFiles extends CollectFiles<FileFilter> {

	public CollectErrorJsonFiles() {
		super.USER_MSG = "\nEnter Upload Error JSON file / Directory path: ";
	}

	public void getAllJsonFiles() {
		getAllDataFiles();

		if (null != jsonDmpfiles && !jsonDmpfiles.isEmpty()) {
			System.out.println("Following files ... : ");
			for (File file : jsonDmpfiles)
				System.out.println(file.getAbsolutePath());

			System.out.println("... will be processed (proceed - y / n): ");

			System.out.flush();
			String userResp = scanner.nextLine();

			if ("y".equalsIgnoreCase(userResp.trim()) || "Y".equalsIgnoreCase(userResp.trim())) {
				UploadVariables.setErrorJsonfiles(jsonDmpfiles);
			} else {

				System.out.print("\nRestart (y / n): ");
				System.out.flush();
				userResp = scanner.nextLine();
				if ("y".equalsIgnoreCase(userResp.trim()) || "Y".equalsIgnoreCase(userResp.trim())) {
					UploadVariables.setErrorJsonfiles(null);
					getAllJsonFiles();
				} else {
					System.exit(0);
				}

			}

		} else {
			System.out.println("No json file found");
		}

		return;
	}


}
