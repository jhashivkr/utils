package com.common.lambda;

import java.util.List;
import java.util.stream.Collectors;

public class CollectionB {

	public static void main(String... args) {

		List<Something> things = Helper.someThings();

		System.err.println("Filter lambda");
		List<Something> filtered = things.stream().parallel()
				.filter(t -> t.getAmount() > 100.00 && t.getAmount() < 1000.00).collect(Collectors.toList());
		System.err.println(filtered);

		System.err.println("Sum lambda");
		double sum = filtered.stream().mapToDouble(t -> t.getAmount()).sum();
		System.err.println(sum);

	}

}
