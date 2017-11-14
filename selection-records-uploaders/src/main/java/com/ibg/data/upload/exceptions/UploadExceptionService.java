package com.ibg.data.upload.exceptions;


public final class UploadExceptionService {

	private static UploadExceptions<ExceptionObjects> upldExceptions = new UploadExceptions<ExceptionObjects>();
	
	public static UploadExceptions<ExceptionObjects> exceptionList(){
		return upldExceptions;
	}
	
	public static void clearExceptions(){
		upldExceptions.emptyExceptions();
	}
	
	 
}
