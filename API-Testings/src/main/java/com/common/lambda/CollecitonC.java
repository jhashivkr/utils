package com.common.lambda;

import java.util.List;

public class CollecitonC {
	
	 public static void main(String... args) {

	        List<Something> things = Helper.someThings();

	        System.err.println("Do something");
	        doSomething(things, new Doer<Something>() {

	            public void doSomething(Something t) {
	                System.err.println(t);
	            }
	        });
	    }

	    public static void doSomething(List<Something> things, Doer<Something> doer) {
	        for (Something s : things) {
	            doer.doSomething(s);
	        }
	    }

}
