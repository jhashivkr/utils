package com.common.test;

import java.util.concurrent.Callable;

public class WordLengthCallable implements Callable<Integer>{
	
	private String word;
	public WordLengthCallable(String word){
		this.word = word;
	}
	
	public int getLength(){
		return word.length();
	}

	@Override
	public Integer call() throws Exception {
		return getLength();
	}

}
