package com.ibg.file.loader;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class DataFiles {

	private static final char EXTENSION_SEPARATOR = '.';
	private static final char UNIX_SEPARATOR = '/';
	private static final char WINDOWS_SEPARATOR = '\\';
	private static final char SYSTEM_SEPARATOR = File.separatorChar;
	private static final String JSON_EXTENSION = "json";
	private static final String FLAT_EXTENSION = "txt";

	private Set<File> dataFiles = new LinkedHashSet<File>();
	private Set<File> flatFiles = new LinkedHashSet<File>();
	private Set<File> jsonFiles = new LinkedHashSet<File>();

	public void addFile(File file) {
		if (null != file) {
			dataFiles.add(file);

			// check extension and put it in appropriate data set
			if (JSON_EXTENSION.equalsIgnoreCase(getExtension(file.getAbsolutePath()))) {
				jsonFiles.add(file);
			} else if (FLAT_EXTENSION.equalsIgnoreCase(getExtension(file.getAbsolutePath()))) {
				flatFiles.add(file);
			}
		}
	}

	protected Set<File> getAllFlatFile() {
		return flatFiles;
	}

	protected Set<File> getAllJsonFile() {
		return jsonFiles;
	}

	public static int indexOfExtension(final String filename) {
		if (filename == null) {
			return -1;
		}
		final int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
		final int lastSeparator = indexOfLastSeparator(filename);
		return lastSeparator > extensionPos ? -1 : extensionPos;
	}

	private static int indexOfLastSeparator(final String filename) {
		if (filename == null) {
			return -1;
		}
		final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
		final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
		return Math.max(lastUnixPos, lastWindowsPos);
	}

	private static String getExtension(final String filename) {
		if (filename == null) {
			return null;
		}
		final int index = indexOfExtension(filename);
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}

}
