package com.ibg.data.upload.exceptions;

import com.ibg.parsers.json.SelectionRecord;


public final class SelRecExceptionService<T> {

	private static UploadExceptionSelRec<SelectionRecord> upldExceptions = new UploadExceptionSelRec<SelectionRecord>();
	
	public static UploadExceptionSelRec<SelectionRecord> exceptionList(){
		return upldExceptions;
	}
	
	public static void clearExceptions(){
		upldExceptions.emptyExceptions();
	}
	
	 
}
