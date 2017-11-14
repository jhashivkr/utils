package com.common.lambda;

import java.util.ArrayList;
import java.util.List;

public class Helper {
	
	public static List<Something> someThings(){
		 List<Something> things = new ArrayList<>();
	        things.add(new Something(99.9));
	        things.add(new Something(199.9));
	        things.add(new Something(299.9));
	        things.add(new Something(399.9));
	        things.add(new Something(1199.9));
	        return things;
	}

}
