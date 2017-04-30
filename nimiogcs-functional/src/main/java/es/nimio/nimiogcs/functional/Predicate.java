package es.nimio.nimiogcs.functional;

import java.util.regex.Pattern;

public interface Predicate<T> 
	// Java 8:
	// extenderá también de java.util.function.Predicate
{

	boolean test(T v);
	
	@Deprecated
	public abstract class _<T> implements Predicate<T> {

		public Predicate<T> and(final Predicate<? super T> other) {
			final Predicate<T> _t = this;
			return new Predicate._<T>() {
				@Override public boolean test(T v) { return _t.test(v) && other.test(v); }
			};
		}

		public Predicate<T> negate() {
			final Predicate<T> _t = this;
			return new Predicate._<T>() {
				@Override public boolean test(T v) { return !_t.test(v); }
			};
		}

		public Predicate<T> or(final Predicate<? super T> other) {
			final Predicate<T> _t = this;
			return new Predicate._<T>() {
				@Override public boolean test(T v) { return _t.test(v) || other.test(v); }
			};
		}
		
		public Predicate<T> xor(final Predicate<? super T> other) {
			final Predicate<T> _t = this;
			return new Predicate._<T>() {
				@Override public boolean test(T v) { return _t.test(v) ^ other.test(v); }
			};
		}
		
		public static <T> Predicate._<T> xt (final Predicate<T> original) {
			return new Predicate._<T> () {
				@Override public boolean test(T v) { return original.test(v); }
				
			};
		}
		
		public final static <T> Predicate<T> True() {
			return new Predicate._<T> () {
				@Override public boolean test(T v) { return true; }
			};
		}

		public final static <T> Predicate<T> False() {
			return new Predicate._<T> () {
				@Override public boolean test(T v) { return false; }
			};
		}
	}
	
	// -------
	
	public final static class Composer {
		
		public static <T> Predicate<T> and (Predicate<T> p1, Predicate<T> p2) {
			return Predicate._.xt(p1).and(p2);
		}
		
		public static <T> Predicate<T> and(Predicate<T> ... predicates) {
			Predicate<T> pf = Predicate._.True();
			for (Predicate<T> predicate : predicates) {
				pf = Composer.and(pf,predicate);
			}
			return pf;
		}
		
		public static <T> Predicate<T> or (Predicate<T> p1, Predicate<T> p2) {
			return Predicate._.xt(p1).or(p2);
		}
		
		public static <T> Predicate<T> or(Predicate<T> ... predicates) {
			Predicate<T> pf = Predicate._.False();
			for (Predicate<T> predicate : predicates) {
				pf = Composer.or(pf,predicate);
			}
			return pf;
		}
		
		public static <T> Predicate<T> xor (Predicate<T> p1, Predicate<T> p2) {
			return Predicate._.xt(p1).xor(p2);
		}

		public static <T> Predicate<T> negate (Predicate<T> p1) {
			return Predicate._.xt(p1).negate();
		}
	}
	
	// --------

	// Integer
	
	public final static class Int {

		/**
		 * Crea un predicado al que se le suminstrará un entero mediante la función identidad.
		 * 
		 * @param n
		 * @return
		 */
		public static _<Integer> eq(final int n) {
			return eq(Function._.<Integer>identity(), n);
		}
		
		public static <T> _<T> eq(final Function<? super T, ? extends Integer> f, final int n) {
			return new _<T>() {
				@Override public boolean test(T t) { return f.apply(t).equals(n); }
			};
		}
		
		public static _<Integer> gt(final int n) { return gt(IntFunction.identity(), n); }
		
		// greater than
		public static <T> _<T> gt(final Function<? super T, ? extends Integer> f, final int n) {
			return new _<T>() {
				@Override public boolean test(T t) { return f.apply(t) > n; }
			};
		}
		
		// less or equal
		public static <T> _<T> le(final Function<? super T, ? extends Integer> f, final int n) {
			return new _<T>() {
				@Override public boolean test(T t) { return f.apply(t) <= n; }
			};
		}
		
		public static _<Integer> lt(final int n) { return lt(IntFunction.identity(), n); }
		
		public static <T> _<T> lt(final Function<? super T, ? extends Integer> f, final int n) {
			return new _<T>() {
				@Override public boolean test(T t) { return f.apply(t) < n; }
			};
		}

		public static _<Integer> isPar() { return isPar(IntFunction.identity()); }
		
		public static <T> _<T> isPar(final Function<? super T, ? extends Integer> f) {
			return new _<T>() {
				@Override public boolean test(T t) { 
					return (f.apply(t) % 2) == 0; 
					}
			};
		}
		
		public static _<Integer> isOdd() { return (_<Integer>) isPar().negate(); }
		public static <T> _<T> isOdd(final Function<? super T, ? extends Integer> f) { return (_<T>) isPar(f).negate(); }
	}

	public final static class Bool {
		
		public static <T> _<T> isTrue(final Function<T, Boolean> f) {
			return new _<T>() {
				@Override public boolean test(T t) { return f.apply(t); }
			};
		}
		
		public static <T> _<T> isFalse(final Function<T, Boolean> f) {
			return (_<T>)isTrue(f).negate();
		}
	}
	
	@Deprecated
	public final static class Str {
		
		public static _<String> eq(final String tx) {
			return eq(Function._.<String>identity(), tx);
		}
		
		public static <T> _<T> eq(final Function<? super T, ? extends String> f, final String tx) {
			return new _<T>() {
				@Override public boolean test(T t) { return f.apply(t).equals(tx); }
			};
		}

		public static _<String> contains(final String tx) {
			return contains(Function._.<String>identity(), tx);
		}
		
		public static <T> _<T> contains(final Function<? super T, ? extends String> f, final String tx) {
			return new _<T>() {
				@Override public boolean test(T v) { return f.apply(v).contains(tx); }
			};
		}

		public static _<String> pattern(final String reggex) { return pattern(Function._.<String>identity(), reggex); }
		public static <T> _<T> pattern(final Function<? super T, ? extends String> f, final String reggex) {
			return new _<T>() {
				@Override public boolean test(T v) { return Pattern.matches(reggex, f.apply(v)); }
			};
		}
	}
}
