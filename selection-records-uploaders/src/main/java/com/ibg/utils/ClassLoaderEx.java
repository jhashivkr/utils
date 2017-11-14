package com.ibg.utils;

import com.ibg.file.loader.DataLoader;

public class ClassLoaderEx {
	
	public static void main(String [] main){
		ClassLoader classLoader = ClassLoaderEx.class.getClassLoader();
		try{
			
			@SuppressWarnings("unchecked")
			Class<DataLoader> aclass = (Class<DataLoader>) classLoader.loadClass("ibg.load.customer.LoadCisUsersData");
			System.out.println("aclass.getName() = " + aclass.getName());
			System.out.println("aclass.getDeclaredMethods() = " + aclass.getDeclaredMethods()[1]);
			
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}

}
