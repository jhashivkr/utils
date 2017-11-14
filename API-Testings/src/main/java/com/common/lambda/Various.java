package com.common.lambda;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class Various {

	public static void main(String... args) {

		List<Something> things = Helper.someThings();

		// Map
		System.err.println(things.stream().map((Something t) -> t.getAmount()).collect(Collectors.toList()));

		// Reduce
		double d = things.stream()
				.reduce(new Something(0.0), (Something t, Something u) -> new Something(t.getAmount() + u.getAmount()))
				.getAmount();
		System.err.println(d);

		// Reduce again
		System.err.println(things.stream()
				.reduce((Something t, Something u) -> new Something(t.getAmount() + u.getAmount())).get());

		// Map/reduce
		System.err.println(things.stream().map((Something t) -> t.getAmount()).reduce(0.0, (x, y) -> x + y));

		// Lazy
		Optional<Something> findFirst = things.stream().filter(t -> t.getAmount() > 1000).findFirst();
		System.err.println(findFirst.get());

		// Lazy no value
		Optional<Something> findFirstNotThere = things.stream().filter(t -> t.getAmount() > 2000).findFirst();
		try {
			System.err.println(findFirstNotThere.get());
		} catch (NoSuchElementException e) {
			System.err.println("Optional was not null, but its value was");
		}
		// Optional one step deeper
		things.stream().filter(t -> t.getAmount() > 1000).findFirst().ifPresent(t -> System.err.println("Here I am"));

	}
}
