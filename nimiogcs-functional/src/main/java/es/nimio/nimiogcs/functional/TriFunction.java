package es.nimio.nimiogcs.functional;

public interface TriFunction<T,U,R,S> {

	S apply(T t, U u, R r);
	
	// ---------
	
	public abstract static class _<T,U,R,S> implements TriFunction<T,U,R,S> {
	
		/**
		 * Devuelve una función currificada de dos parámetros, como resultado 
		 * de fijar un valor determinado para el primer parámetro.
		 * 
		 * @param vfix
		 * @return
		 */
		public BiFunction._<U,R,S> currify(final T vfix) {
			final TriFunction<T,U,R,S> _t = this;
			return new BiFunction._<U,R,S>() {
				@Override public S apply(U u, R r) { return _t.apply(vfix, u, r); }
			};
		}
		
		/**
		 * Devuelve una versión de ésta función con los parámetros invertidos.
		 * Útil para currificar por el otro parámetro.
		 * @return
		 */
		public TriFunction._<R,U,T,S> revertPars() {
			final TriFunction<T,U,R,S> _t = this;
			return new TriFunction._<R, U, T, S> () {
				@Override public S apply(R r, U u, T t) { return _t.apply(t, u, r); }
			};
		}
		
		/**
		 * Devuelve una variante de esta función que espera, en lugar de tres parámetros,
		 * un parámetro de tipo tupla y devuelve el resultado esperado.
		 */
		public Function._<Tuples.T3<T, U, R>, S> pack() {
			final TriFunction<T, U, R, S> _t = this;
			return new Function._<Tuples.T3<T,U, R>, S> () {
				@Override public S apply(Tuples.T3<T, U, R> v) { return _t.apply(v._1, v._2, v._3); }
			};
		}
	}
	
}
