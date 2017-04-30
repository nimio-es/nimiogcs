package es.nimio.nimiogcs.functional;

/**
 * Provee un elemento de forma perezosa.
 *
 * @param <T>
 */
public interface Lazy<T> {

	T eval ();


	/**
	 * Versión de la evaluación perezosa que memoriza el valor tras la primera
	 * ejecución. 
	 *
	 * @param <T>
	 */
	public static class Memoize<T> implements Lazy<T> {

		private boolean evaluated = false;
		private T value;
		private Lazy<T> _lazy;
		
		// NOTA: solamente accesible a los del mismo paquete (p.e. los tests)
		Lazy<T> getLazy() {
			return _lazy;
		}
		
		@SuppressWarnings("unchecked")
		public Memoize(Lazy<? extends T> lazy) {
			_lazy = (Lazy<T>)lazy;
		}
		
		@Override
		public T eval() {
			if(!evaluated) {
				value = _lazy.eval();
				evaluated = true;
			}
			return value;
		}
	}
	
	/**
	 * Versión para facilitar el trabajo con constantes.
	 *
	 * @param <K>
	 */
	public final static class Constant<K> implements Lazy<K> {
		
		private K k;
		
		public Constant(K k) {
			this.k = k;
		}

		@Override public K eval() { return this.k; }
	}
	
	
	/**
	 * Para funciones u operaciones que son pesadas y es mejor no procesar 
	 * si no llega a ser estrictamente necesario.
	 *
	 * @param <T>
	 * @param <R>
	 */
	public final static class LazyFunctionEval<T, R> extends Memoize<R> {
		
		public LazyFunctionEval(final Function<? super T, ? extends R> f, final T v) {
			super(new Lazy<R>() {
				@Override public R eval() { return f.apply(v); }
			});
		}
	}
}
