package com.common.test;

public class AddProcessor implements Runnable{
	
	private int v1, v2;
	
	public AddProcessor(int v1, int v2){
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public void run(){
		add();
	}
	public void add(){
		System.out.println(v1 + v2);
	}
}