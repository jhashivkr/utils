package com.ibg.utils;

import java.io.File;

public class FileFilterUtil {
	public final static String xls = "xls";
	public final static String xlsx = "xlsx";
	public final static String vm = "vm";
	public final static String json = "json";
	public final static String txt = "txt";
	public final static String done = "done";

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static String getExtension(String str) {
		String ext = null;
		int i = str.lastIndexOf('.');

		if (i > 0 && i < str.length() - 1) {
			ext = str.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static String getNameWithoutExtension(File f) {
		String name = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			name = s.substring(0, i);
		}
		return name;
	}

	public static String getNameWithoutExtension(String str) {
		String name = null;
		int i = str.lastIndexOf('.');

		if (i > 0 && i < str.length() - 1) {
			name = str.substring(0, i);
		}
		return name;
	}

	public static String getBaseGrpName(File f) {
		String name = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			name = s.substring(0, i);
		}

		i = name.lastIndexOf('_');
		if (i > 0 && i < name.length() - 1) {
			name = name.substring(0, i);
		}

		return name;
	}

	public static String getBaseGrpName(String str) {
		String name = null;
		int i = str.lastIndexOf('.');

		if (i > 0 && i < str.length() - 1) {
			name = str.substring(0, i);
		}

		i = name.lastIndexOf('_');
		if (i > 0 && i < name.length() - 1) {
			name = name.substring(0, i);
		}

		return name;
	}

	public static boolean isDoneFile(String fileName) {
		if (null == fileName) {
			return false;
		}
		if (done.equalsIgnoreCase(getExtension(fileName))) {
			return true;
		}
		return false;

	}

}
