package com.common.lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LambdaTest {

	List<Earthling> earthlingList = new ArrayList<>();

	public static void main(String[] args) {
		// Runnable r = () -> System.out.println("Hello World");
		// r.run();

		// new Thread(() -> System.out.println("Hello World from
		// Thread")).start();

		LambdaTest lambdaTest = new LambdaTest();
		lambdaTest.createEarthlingList();

		System.out.println("earthlingList: before sort=>" + lambdaTest.earthlingList);
		lambdaTest.shortEarthlingList();

		System.out.println("earthlingList: after sort=>" + lambdaTest.earthlingList);

	}

	public void createEarthlingList() {
		Earthling e1 = new Earthling();
		e1.setName("shiv");
		e1.setAge(42);
		e1.setGender("male");

		Earthling e2 = new Earthling();
		e2.setName("pushpa");
		e2.setAge(37);
		e2.setGender("female");

		Earthling e3 = new Earthling();
		e3.setName("pragya");
		e3.setAge(13);
		e3.setGender("female");

		Earthling e4 = new Earthling();
		e4.setName("ankit");
		e4.setAge(7);
		e4.setGender("male");

		earthlingList.add(e1);
		earthlingList.add(e2);
		earthlingList.add(e3);
		earthlingList.add(e4);

	}

	private void shortEarthlingList() {
		Collections.sort(earthlingList, (Earthling e1, Earthling e2) -> (e1.getAge() > e2.getAge()) ? 1 : 0);
	}

	class Earthling {
		private String name;
		private int age;
		private String gender;

		public Earthling() {

		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + age;
			result = prime * result + ((gender == null) ? 0 : gender.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Earthling other = (Earthling) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (age != other.age)
				return false;
			if (gender == null) {
				if (other.gender != null)
					return false;
			} else if (!gender.equals(other.gender))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Earthling [name=" + name + ", age=" + age + ", gender=" + gender + "]";
		}

		private LambdaTest getOuterType() {
			return LambdaTest.this;
		}

	}

}
