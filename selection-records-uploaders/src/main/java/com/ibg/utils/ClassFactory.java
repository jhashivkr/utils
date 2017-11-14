package com.ibg.utils;

import java.net.URL;
import java.net.URLClassLoader;

import com.ibg.file.loader.DataLoader;

public class ClassFactory extends ClassLoader{
	
	/*
	
	public static DataLoader newInstance(String classz){
		URLClassLoader tmp = new URLClassLoader(new URL[]{classz}){
			public Class loadClass(String name){
				return super.loadClass(name);
			}
		};
		
		return (DataLoader) tmp.loadClass(name);
	}
	*/

}
