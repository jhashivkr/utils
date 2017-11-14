package com.ibg.data.upload.exceptions;

import java.util.LinkedList;
import java.util.List;

public class UploadExceptions<T extends ExceptionObjects> {

	private List<T> exceptions;

	public UploadExceptions() {
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
