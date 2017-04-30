package es.nimio.nimiogcs.functional;

import java.util.regex.Pattern;

/***
 * Utilidades y extensiones para Predicado.
 */
public final class Predicates {

	/**
	 * Versión del predicado que ofrece métodos para componer.
	 * 
	 * @param <T>
	 */
	public static abstract class Extended<T> implements Predicate<T> {

		public Extended<T> and(final Predicate<? super T> other) {
			final Extended<T> _t = this;
			return new Extended<T>() {
				@Override
				public boolean test(T v) {
					return _t.test(v) && other.test(v);
				}
			};
		}

		public Extended<T> negate() {
			final Extended<T> _t = this;
			return new Extended<T>() {
				@Override
				public boolean test(T v) {
					return !_t.test(v);
				}
			};
		}

		public Extended<T> or(final Predicate<? super T> other) {
			final Extended<T> _t = this;
			return new Extended<T>() {
				@Override
				public boolean test(T v) {
					return _t.test(v) || other.test(v);
				}
			};
		}

		public Extended<T> xor(final Predicate<? super T> other) {
			final Extended<T> _t = this;
			return new Extended<T>() {
				@Override
				public boolean test(T v) {
					return _t.test(v) ^ other.test(v);
				}
			};
		}

		public static <T> Extended<T> of(final Predicate<T> original) {
			return new Extended<T>() {
				@Override
				public boolean test(T v) {
					return original.test(v);
				}

			};
		}
	}

	/***
	 * Predicado que siempre devolverá "True"
	 * 
	 * @return
	 */
	public static final <T> Extended<T> True() {
		return new Extended<T>() {
			@Override
			public boolean test(T v) {
				return true;
			}
		};
	}

	/**
	 * Predicado que siempre devolverá "False"
	 */
	public static final <T> Extended<T> False() {
		return new Extended<T>() {
			@Override
			public boolean test(T v) {
				return false;
			}
		};
	}

	/**
	 * Utilidad que facilta la composición de predicados
	 */
	public static final class Composer {

		public static <T> Extended<T> and(Predicate<T> p1, Predicate<T> p2) {
			return Extended.of(p1).and(p2);
		}

		public static <T> Extended<T> and(Predicate<T>... predicates) {
			Extended<T> pf = True();
			for (Predicate<T> predicate : predicates) {
				pf = Composer.and(pf, predicate);
			}
			return pf;
		}

		public static <T> Extended<T> or(Predicate<T> p1, Predicate<T> p2) {
			return Extended.of(p1).or(p2);
		}

		public static <T> Extended<T> or(Predicate<T>... predicates) {
			Extended<T> pf = False();
			for (Predicate<T> predicate : predicates) {
				pf = Composer.or(pf, predicate);
			}
			return pf;
		}

		public static <T> Extended<T> xor(Predicate<T> p1, Predicate<T> p2) {
			return Extended.of(p1).xor(p2);
		}

		public static <T> Extended<T> negate(Predicate<T> p1) {
			return Extended.of(p1).negate();
		}
	}

	/**
	 * Utilidades para facilitar la creación de predicados basados en cadenas de texto.
	 */
	public final static class Str {

		public static Extended<String> eq(final String tx) {
			return eq(Function._.<String> identity(), tx);
		}
		
		public static <T> Extended<T> eq(final Function<? super T, ? extends String> f, final String tx) {
			return new Extended<T>() {
				@Override public boolean test(T t) { return f.apply(t).equals(tx); }
			};
		}

		public static Extended<String> contains(final String tx) {
			return contains(Function._.<String> identity(), tx);
		}

		public static <T> Extended<T> contains(final Function<? super T, ? extends String> f, final String tx) {
			return new Extended<T>() {
				@Override public boolean test(T v) { return f.apply(v).contains(tx); }
			};
		}

		public static Extended<String> pattern(final String reggex) {
			return pattern(Function._.<String> identity(), reggex);
		}

		public static <T> Extended<T> pattern(final Function<? super T, ? extends String> f, final String reggex) {
			return new Extended<T>() {
				@Override public boolean test(T v) { return Pattern.matches(reggex, f.apply(v)); }
			};
		}
		
		public static <T> Extended<T> nullOrWhiteSpaces(final Function<? super T, ? extends String> f) {
			return new Extended<T>() {
				@Override public boolean test(T t) {
					String v = f.apply(t);
					return v == null || v.trim().equals("");
				}
			};
		}
		
		public static Extended<String> nullOrWhiteSpaces() {
			return new Extended<String>() {
				@Override public boolean test(String s) { return s == null || s.trim().equals(""); }
			};
		}
	}
}
