package tk.luoxing123.utils;

import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.EnumSet;
import java.util.ArrayList;
import java.util.List;

public class Collectors<T> {
	static public <T> ToCountMapCollector<T> toMap() {
		return new ToCountMapCollector<T>();
	}

	static public PrimeNumbersCollector toPrimeList() {
		return new PrimeNumbersCollector();
	}

	static public class PrimeNumbersCollector implements
			Collector<Integer, List<Integer>, List<Integer>> {
		public Supplier<List<Integer>> supplier() {
			return () -> new ArrayList<Integer>();
		}

		@Override
		public BiConsumer<List<Integer>, Integer> accumulator() {
			return (List<Integer> acc, Integer can) -> {
				if (isPrime(acc, can)) {
					acc.add(can);
				}
			};
		}

		static boolean isPrime(List<Integer> lst, Integer can) {
			return lst.stream().noneMatch(i -> can % i == 0);
		}

		@Override
		public BinaryOperator<List<Integer>> combiner() {
			return (List<Integer> lst, List<Integer> other) -> {
				lst.addAll(other);
				return lst;
			};
		}

		@Override
		public Function<List<Integer>, List<Integer>> finisher() {
			return i -> i;
		}

		@Override
		public Set<Characteristics> characteristics() {
			return Collections.unmodifiableSet(EnumSet
					.of(Characteristics.IDENTITY_FINISH));
		}
	}

	static public class ToCountMapCollector<T> implements
			Collector<T, Map<T, Integer>, Map<T, Integer>> {
		@Override
		public Supplier<Map<T, Integer>> supplier() {
			return () -> new HashMap<T, Integer>();
		}

		@Override
		public Function<Map<T, Integer>, Map<T, Integer>> finisher() {
			return i -> i;
		}

		@Override
		public BiConsumer<Map<T, Integer>, T> accumulator() {
			return (map, str) -> {
				if (!map.containsKey(str)) {
					map.put(str, 1);
				} else {
					map.put(str, map.get(str) + 1);
				}
			};
		}

		@Override
		public BinaryOperator<Map<T, Integer>> combiner() {
			return (map1, map2) -> {
				map1.putAll(map2);
				return map1;
			};
		}

		@Override
		public Set<Characteristics> characteristics() {
			return Collections.unmodifiableSet(EnumSet
					.of(Characteristics.IDENTITY_FINISH,
							Characteristics.CONCURRENT));
		}

	}

}
