package com.ibg.data.upload.exceptions;

import java.util.LinkedList;
import java.util.List;

import com.ibg.parsers.json.SelectionRecord;

public class UploadExceptionSelRec<T extends SelectionRecord> {

	private List<T> exceptions;

	public UploadExceptionSelRec() {
		exceptions = new LinkedList<T>();
	}

	public void add(T msgObj) {
		exceptions.add(msgObj);
	}

	public List<T> getExceptions() {
		return exceptions;
	}

	public void emptyExceptions() {
		if (!exceptions.isEmpty())
			exceptions.clear();
	}
}
