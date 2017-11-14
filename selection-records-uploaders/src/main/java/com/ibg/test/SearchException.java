package com.ibg.test;


public class SearchException extends Exception
{
	private static final long serialVersionUID = 1810771363578991223L;
	
	public SearchException(String message)
	{
		super(message);
	}

	public SearchException(String message, Throwable th)
	{
		super(message, th);
	}

	public SearchException(Throwable th)
	{
		super("A Problem Occured While Searching.", th);
	}
}
