package es.nimio.nimiogcs.functional.stream;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;

import es.nimio.nimiogcs.functional.BiFunction;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Lazy;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;

public interface Stream<A> extends Iterable<A> {

	/**
	 * Devuelve un Stream asociado donde se mapean los elementos a un valor destino.
	 * @param f
	 * @return
	 */
	A getHead();
	Stream<A> getTail();
	
	boolean isEmpty();

	Stream<A> prepend(A element);
	Stream<A> prepend(Stream<? extends A> stream);

	Stream<A> append(A element);
	Stream<A> append(Lazy<Stream<? extends A>> lazy);
	Stream<A> append(Stream<? extends A> stream);
	
	/**
	 * Confirma que existe al menos un elemento que satisface el predicado.
	 * @param p
	 * @return
	 */
	Boolean exists(Predicate<? super A> p);
	
	/**
	 * Confirma que todos los elementos del stream satisfacen el predicado.
	 * @param p
	 * @return
	 */
	Boolean forAll(Predicate<? super A> p);
	
	/**
	 * Devuelve un Stream eliminando los n primeros.
	 * @param n
	 * @return
	 */
	Stream<A> skip(int n);

	/**
	 * Toma los primeros elementos del stream.
	 * @param n
	 * @return
	 */
	Stream<A> take(int n);

	/**
	 * Devuelve elementos mientras se cumpla el predicado.
	 * @param p
	 * @return
	 */
	Stream<A> takeWhile(Predicate<? super A> p);
	
	/**
	 * Devuelve un stream filtrado usando el predicado.
	 * 
	 * @param p
	 * @return
	 */
	Stream<A> filter(Predicate<? super A> p);

	/**
	 * Proyecta los elementos del Stream<A> transformándolos en elementos de tipo B
	 * mediante la función f.
	 * 
	 * @param f
	 * @return
	 */
	<B> Stream<B> map(Function<? super A, ? extends B> f);
	
	/**
	 * Calcula el acumulado procesando el Stream desde la izquierda
	 * hacia la derecha.
	 * @param acc - valor inicial del acumulador
	 * @param facc - función de acumulado
	 * @return
	 */
	<B> B foldLeft(B acc, BiFunction<? super B, ? super A, ? extends B> facc);
	
	/**
	 * Proyecta el stream como una enumeración
	 * @return
	 */
	Enumeration<A> getEnumeration();
	
	// ------------
	
	@Deprecated()
	public final static class _ {
		
		public static <A> Stream<A> empty() {
			return new Empty<A>();
		}
		
		public static <A> Stream<A> cons(Lazy<A> head) {
			return _.cons(head, new Lazy<Stream<A>>() { 
				@Override public Stream<A> eval() { return _.empty(); } });
		}

		public static <A> Stream<A> cons(Lazy<A> head, Lazy<Stream<A>> tail) {
			return new Cons<A>(head, tail);
		}
		
		public static <A> Stream<A> of(A... pars) {
			return of(Arrays.asList(pars));
		}
		
		/**
		 * Genera un stream como rango con infinitos valores.

		 * @return
		 */
		public static Stream<Integer> intRange(int initVal) {
			return intRange(initVal, Integer.MAX_VALUE);
		}
		
		public static Stream<Integer> intRange(int initVal, int endVal) {
			if (initVal <= endVal) return intRange(initVal, endVal, 1);
			else return intRange(initVal, endVal, -1);
		}
		
		public static Stream<Integer> intRange(final int initVal, final int endVal, final int step) {
			
			final Function<Tuples.T2<Integer,Boolean>, Integer> getValue = new Function<Tuples.T2<Integer,Boolean>, Integer>() {
				@Override public Integer apply(T2<Integer, Boolean> v) {
					return v._1 + step;
				}
			};
			
			final Function<Tuples.T2<Integer,Boolean>, Boolean> hasValue = new Function<Tuples.T2<Integer,Boolean>, Boolean>() {
				@Override public Boolean apply(T2<Integer, Boolean> v) {
					int proximo = getValue.apply(v);
					return v._2 ? // orden ascendente
							(proximo > initVal) && (proximo <= endVal)
							: // orden descendente 
							(proximo < initVal) && (proximo >= endVal)
							;
				}
			};
			
			final Function<Tuples.T2<Integer, Boolean>, Tuples.T2<Integer, Boolean>> nextState = new Function<Tuples.T2<Integer, Boolean>, Tuples.T2<Integer, Boolean>>() {
				@Override public T2<Integer, Boolean> apply(T2<Integer, Boolean> v) {
					return v.withFirst(getValue.apply(v));
				}
			};
			
			return cons(
					new Lazy.Constant<Integer>(initVal),
					new LazyTailWithState<Tuples.T2<Integer,Boolean>, Integer>(
							Tuples.tuple(initVal, (endVal >= initVal) && (step >= 0)), 
							getValue,							
							hasValue, 
							nextState, 
							false)
					);
		}
		
		/**
		 * Crea un stream infinito repitiendo siempre el mismo valor.
		 * @return
		 */
		public static <A> Stream<A> repeat(A value) {
			final Function<A,A> getValue = Function._.identity();
			final Function<A,Boolean> hasValue = new Function<A,Boolean>() {
				@Override public Boolean apply(A value) { return true; }};
			final Function<A,A> nextState = Function._.identity();
			
			return new LazyTailWithState<A,A>(value, getValue, hasValue, nextState, false).eval();
		}
		
		// --------------------------
		
		/**
		 * Creates a new Stream from an iterable.
		 * @param it
		 * @return
		 */
		public static <A> Stream<A> of(Iterable<A> it) {
			return of(it.iterator());
		}

		public static <A> Stream<A> of(Iterator<A> it) {

			final Function<Iterator<A>, A> getValue = new Function<Iterator<A>, A>() {
				@Override public A apply(Iterator<A> v) { return v.next(); }
			};
			
			final Function<Iterator<A>, Boolean> hasValue = new Function<Iterator<A>, Boolean>() {
				@Override public Boolean apply(Iterator<A> v) { return v.hasNext(); }
			};
			
			// el propio iterador mantiene la información de estado.
			final Function<Iterator<A>, Iterator<A>> nextState = Function._.identity();
			
			return new LazyTailWithState<Iterator<A>,A>(
							it,
							getValue,
							hasValue,
							nextState,
							true)
					.eval();
		}
	}
	
	/**
	 * Caso Strem.Empty
	 */
	final static class Empty<A> implements Stream<A> {

		protected Empty() {}
		
		@Override public A getHead() { return null; }
		@Override public Stream<A> getTail() { return new Empty<A>(); }
		@Override public boolean isEmpty() { return true; }

		@Override public Stream<A> prepend(A element) { 
			return prepend(new Cons<A>(new Lazy.Constant<A>(element)));
		}

		@SuppressWarnings("unchecked")
		@Override public Stream<A> prepend(Stream<? extends A> stream) {
			return (Stream<A>)stream;
		}		

		@Override public Stream<A> append(A element) {
			return append(new Cons<A>(new Lazy.Constant<A>(element)));
		}

		@SuppressWarnings("unchecked")
		@Override public Stream<A> append(Stream<? extends A> stream) {
			return (Stream<A>)stream;
		}
		
		@SuppressWarnings("unchecked")
		@Override public Stream<A> append(Lazy<Stream<? extends A>> lazy) {
			return (Stream<A>)lazy.eval();
		}
		

		@Override public Boolean exists(Predicate<? super A> p) { return false; }
		@Override public Boolean forAll(Predicate<? super A> p) { return false; }
		@Override public Stream<A> skip(int n) { return this; }
		@Override public Stream<A> take(int n) { return this; }
		@Override public Stream<A> takeWhile(Predicate<? super A> p) { return this; }
		@Override public Stream<A> filter(Predicate<? super A> p) { return this; }
		@Override public <B> Stream<B> map(Function<? super A, ? extends B> f) { return new Empty<B>(); }

		@Override public <B> B foldLeft(B acc, BiFunction<? super B, ? super A, ? extends B> facc) { return acc; }
		
		// -- iterable
		@Override public Iterator<A> iterator() { return new StreamIterator<A>(this); }
		
		// -- enumeration
		@SuppressWarnings("unchecked")
		@Override public Enumeration<A> getEnumeration() { return (Enumeration<A>) iterator(); }

	}
	
	final static class Cons<A> implements Stream<A> {

		private Lazy<? extends A> _head;
		private Lazy<? extends Stream<A>> _tail;

		protected Cons(Lazy<? extends A> head) {
			_head = head;
			_tail = new Lazy<Stream<A>>() {
				@Override public Stream<A> eval() { return new Empty<A>(); }
			};
		}
		
		protected Cons(Lazy<? extends A> head, Lazy<? extends Stream<A>> tail) 
		{
			_head = head;
			_tail = tail;
		}
		
		@Override public boolean isEmpty() { return false; }

		@Override public A getHead() { return _head.eval(); }
		@Override public Stream<A> getTail() { return _tail.eval(); }

		@Override public Stream<A> prepend(A element) { 
			return prepend(new Cons<A>(new Lazy.Constant<A>(element)));
		}
		@SuppressWarnings("unchecked")
		@Override public Stream<A> prepend(Stream<? extends A> stream) {
			return ((Stream<A>)stream).append(this);
		}
		
		@Override public Stream<A> append(A element) {
			return append(new Cons<A>(new Lazy.Constant<A>(element)));
		}
		@Override
		public Stream<A> append(final Stream<? extends A> stream) {
			final Cons<A> _t = this;
			return new Cons<A>(
					this._head,
					new Lazy<Stream<A>>() {
						@Override public Stream<A> eval() { return _t.getTail().append(stream); } 
					});
		}
		
		@Override public Stream<A> append(final Lazy<Stream<? extends A>> lazy) {
			final Cons<A> _t = this;
			return new Cons<A>(
					this._head,
					new Lazy<Stream<A>>() {
						@Override public Stream<A> eval() { return _t.getTail().append(lazy); } 
					});
		}

		@Override public Boolean exists(Predicate<? super A> p) {
			if (p.test(this.getHead())) return true; // detenemos tan pronto encontramos el primer caso positivo.
			else return this.getTail().exists(p);
		}

		@Override public Boolean forAll(Predicate<? super A> p) {
			if (!p.test(this.getHead())) return false; // detenemos tan pronto encontramos el primer caso negativo.
			else {
				Stream<A> tl = this.getTail();
				if(tl.isEmpty()) return true;  // si hemos llegado al final es que es OK
				return tl.forAll(p); // en caso contrario se lo pasamos al siguiente.
			}
		}
		
		@Override
		public Stream<A> skip(int n) {
			if (n <= 0) return this;
			else return this.getTail().skip(n - 1);
			// TODO: Hacer versión "gentil" con la pila
		}
		
		@Override
		public Stream<A> take(final int n) {
			if (n <= 0) return new Empty<A>();
			final Stream<A> _t = this;
			return new Cons<A>(
					this._head,
					new Lazy<Stream<A>>() {
						@Override public Stream<A> eval() {
							return _t.getTail().take(n - 1);
						}
					});
		}
		
		@Override
		public Stream<A> takeWhile(final Predicate<? super A> p) {
			if(!p.test(getHead())) return new Empty<A>();
			final Stream<A> _t = this;
			return new Cons<A>(
					this._head,
					new Lazy<Stream<A>>() {
						@Override public Stream<A> eval() {
							return _t.getTail().takeWhile(p);
						}
					});
		}

		@Override
		public Stream<A> filter(final Predicate<? super A> p) {
			final A v = this.getHead();
			// condición de exclusión, pasando al siguiente.
			// TODO: Hacer versión gentil con la pila.
			if(!p.test(v)) return this.getTail().filter(p); 
			
			// si, por el contrario, sí pasa el filtro, devolvemos una estructura perezosa,  
			// esperando al siguiente elemento para filtrar.
			final Cons<A> _t = this;
			return new Cons<A>(
					new Lazy.Constant<A>(v),
					new Lazy.Memoize<Stream<A>>(
							new Lazy<Stream<A>>() {
								@Override public Stream<A> eval() {
									return _t.getTail().filter(p);
								}
							}
					));
		}
		
		@Override
		public <B> Stream<B> map(final Function<? super A, ? extends B> f) {
			final Cons<A> _t = this;
			return new Cons<B>(
					new Lazy.LazyFunctionEval<A, B>(f, _t.getHead()),
					// los mapeos los "memorizaremos"
					new Lazy.Memoize<Stream<B>>(
						new Lazy<Stream<B>>() {
							@Override public Stream<B> eval() {
								return _t.getTail().map(f);
							}
						}
					)); 
		}

		// --- iterable
		
		@Override public Iterator<A> iterator() { return new StreamIterator<A>(this); }

		@SuppressWarnings("unchecked")
		@Override public Enumeration<A> getEnumeration() { return (Enumeration<A>) iterator(); }

		@Override
		public <B> B foldLeft(B acc, BiFunction<? super B, ? super A, ? extends B> facc) { 
			return getTail().foldLeft(facc.apply(acc, getHead()), facc);
			// TODO: implementar una versión más gentil con la pila
		}
	}
	
	// ---------------------------------
	
	final static class LazyTailWithState<S,A> implements Lazy<Stream<A>> {

		// TODO: Implementar en el futuro con Option<S>
		private final S _state;
		private final Function<? super S,? extends A> _getValue;
		private final Function<? super S, Boolean> _hasValue;
		private final Function<? super S,? extends S> _nextState;
		private final boolean _memoizeIt;
		
		public LazyTailWithState(
				S initialState,
				Function<? super S,? extends A> fGetValue,
				Function<? super S, Boolean> fHasValue,
				Function<? super S,? extends S> fNextState,
				boolean memoize) {
			_state = initialState;
			_getValue = fGetValue;
			_hasValue = fHasValue;
			_nextState = fNextState;
			_memoizeIt = memoize;
		}
		
		@Override
		public Stream<A> eval() {
			// si, efectivamente, hay estado posible, devolvemos nuevo Stream<A>.
			if(_hasValue.apply(_state)) {
				A _newValue = _getValue.apply(_state);
				Lazy<Stream<A>> _nt = new LazyTailWithState<S, A>(_nextState.apply(_state), _getValue, _hasValue, _nextState, _memoizeIt);
				return Stream._.cons(
						new Lazy.Constant<A>(_newValue),
						// ? requiere mantener el valor para próximos usos
						_memoizeIt 
							? new Lazy.Memoize<Stream<A>>(_nt)
							// no requiere memorizado de resultados	
							: _nt
						);
			}
			else
				return new Empty<A>();
		}
	}

	// ---------------------------------

	// el mismo iterador me va a servir como enumeración
	final class StreamIterator<E> implements Iterator<E>, Enumeration<E> {

		private Stream<E> _stream;
		
		public StreamIterator(Stream<E> stream) { this._stream = stream; }
		
		@Override
		public boolean hasNext() {
			return !_stream.isEmpty();
		}

		@Override
		public E next() {
			E _next = _stream.getHead();
			_stream = _stream.getTail();
			return _next;
		}

		// para la enumeración usamos los del iterador
		
		@Override
		public boolean hasMoreElements() {
			return hasNext();
		}

		@Override
		public E nextElement() {
			return next();
		}

		@Override
		public void remove() {
			throw new java.lang.UnsupportedOperationException("Operación no soportada");			
		}
	}
}
