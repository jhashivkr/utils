package com.thread.test;

import java.util.LinkedList;
import java.util.List;

import ibg.cc.mq.listener.MessageObject;

public class DataStore {
	
	private static List<MessageObject> store  = new LinkedList<>(); 
	
	public static void addObj(MessageObject obj){
		store.add(obj);
	}
	
	public static int size(){
		return store.size();
	}

}
