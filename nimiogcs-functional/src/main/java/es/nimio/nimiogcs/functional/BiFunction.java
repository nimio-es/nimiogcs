package es.nimio.nimiogcs.functional;

import es.nimio.nimiogcs.functional.Tuples.T2;

public interface BiFunction<T,U,R> {

	R apply(T t, U u);
	
	// ---------
	
	public abstract static class _<T,U,R> implements BiFunction<T,U,R> {
	
		public static <T,U,R> _<T,U,R> of(final BiFunction<T,U,R> original) {
			return new BiFunction._<T,U,R>() {
				@Override
				public R apply(T t, U u) {
					return original.apply(t, u);
				}
			};
		}
		
		/**
		 * Devuelve una función que primero aplica ésta y posteriormete aplica la función
		 * facilitada al resultado de la primera.
		 * 
		 * @param after
		 * @return
		 */
		public <V> BiFunction._<T,U,V> andThen(final Function<? super R,? extends V> after) {
			final _<T,U,R> _t = this;
			return new BiFunction._<T,U,V>() {
				@Override public V apply(T t, U u) {
					return after.apply(_t.apply(t, u));
				}
			};
		}
		
		/**
		 * Devuelve una función currificada, como resultado de fijar un valor determinado 
		 * para el primer parámetro.
		 * 
		 * @param vfix
		 * @return
		 */
		public Function._<U,R> currify(final T vfix) {
			final BiFunction<T,U,R> _t = this;
			return new Function._<U,R>() {
				@Override public R apply(U v) { return _t.apply(vfix, v); }
			};
		}
		
		/**
		 * Devuelve una versión de ésta función con los parámetros invertidos.
		 * Útil para currificar por el otro parámetro.
		 * @return
		 */
		public BiFunction._<U,T,R> revertPars() {
			final BiFunction<T,U,R> _t = this;
			return new BiFunction._<U, T, R> () {
				@Override public R apply(U u, T t) { return _t.apply(t, u); }
			};
		}
		
		/**
		 * Devuelve una variante de esta función que espera, en lugar de dos parámetros
		 * un parámetro de tipo tupla y devuelve el resultado esperado.
		 */
		public Function._<Tuples.T2<T, U>, R> pack() {
			final BiFunction<T, U, R> _t = this;
			return new Function._<Tuples.T2<T,U>, R> () {
				@Override public R apply(T2<T, U> v) { return _t.apply(v._1, v._2); }
			};
		}
		
	}
	
}
