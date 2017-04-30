package es.nimio.nimiogcs.functional.stream;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Lazy;
import es.nimio.nimiogcs.functional.Optional;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.stream.Stream.Cons;
import es.nimio.nimiogcs.functional.stream.Stream.Empty;
import es.nimio.nimiogcs.functional.stream.Stream.LazyTailWithState;

/**
 * Utilidades relativas a Steam
 */
public final class Streams {

	// --------------------------------------------------------
	// Creación ...
	// --------------------------------------------------------
	
	/**
	 * Crea un Stream vacío.
	 */
	public static <A> Stream<A> empty() {
		return new Empty<A>();
	}

	/**
	 * Crea una estructura Cons (Stream) pasando un valor lazy como cabeza.
	 * @param head
	 * @return
	 */
	public static <A> Stream<A> cons(Lazy<A> head) {
		return cons(head, new Lazy<Stream<A>>() { 
			@Override public Stream<A> eval() { return empty(); } });
	}

	/**
	 * Crea una estructura Cons (Stream) pasando valores lazy para la cabeza y la cola.
	 * @param head
	 * @param tail
	 * @return
	 */
	public static <A> Stream<A> cons(Lazy<A> head, Lazy<Stream<A>> tail) {
		return new Cons<A>(head, tail);
	}
	
	/**
	 * A partir de un array. 
	 */
	public static <A> Stream<A> of(A... pars) {
		return of(Arrays.asList(pars));
	}
	
	/**
	 * Creates a new Stream from an iterable.
	 * @param it
	 * @return
	 */
	public static <A> Stream<A> of(Iterable<A> it) {
		if (it == null) return empty();
		return of(it.iterator());
	}

	public static <A> Stream<A> of(Iterator<A> it) {

		if (it == null) return empty();
		
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
	
	/**
	 * Localiza un elemento en un stream. Si lo encuentra devuelve Some(A), 
	 * en caso contrario devuelve None.
	 * 
	 * @param stream
	 * @param p
	 * @return
	 */
	public static <A> Optional<A> find(Stream<A> stream, Predicate<A> p)  {
		
		// el caso de salida
		if(stream.isEmpty()) return Optional._.of((A)null);
		
		// evaluamos la cabecera
		if(p.test(stream.getHead())) return Optional._.of(stream.getHead());
		
		// continuamos con el resto
		return find(stream.getTail(), p);
	}
	
	/**
	 * Transforma un Stream en un array
	 */
	public static <A> A[] toArray(Stream<A> stream) {
		if(stream==null)
				throw new IllegalArgumentException("'stream' must be not null'");
		List<A> lista = Collections.list(stream.getEnumeration()); 
		@SuppressWarnings("unchecked")
		A[] r = (A[]) Array.newInstance(lista.get(0).getClass(), new int[] {lista.size()});
		return lista.toArray(r);
	}
	
}
