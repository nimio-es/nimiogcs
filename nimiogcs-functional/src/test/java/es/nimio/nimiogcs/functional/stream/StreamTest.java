package es.nimio.nimiogcs.functional.stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import es.nimio.nimiogcs.functional.BiFunction;
import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.IntFunction;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.Tuples;
import es.nimio.nimiogcs.functional.Tuples.T2;
import es.nimio.nimiogcs.functional.stream.Stream;

public class StreamTest {

	@Test
	public void StreamOfIntegersPreppendingElements() {
		
		// creamos un stream usando elementos constantes
		// en realidad se lee en orden inverso 30 :: 20 :: 10 :: empty
		Stream<Integer> s = Stream._.<Integer>empty()
				.prepend(10)
				.prepend(20)
				.prepend(30);
		
		// no puede ser nulo
		assertNotNull(s);
		
		int h1 = s.getHead();
		int h2 = s.getTail().getHead();
		int h3 = s.getTail().getTail().getHead();
		Stream<Integer> h4 = s.getTail().getTail().getTail();
		
		// empezamos confirmando que el último debe ser vacío
		assertTrue(h4.isEmpty());
		
		// y que el resto realmente está al revés
		assertEquals(10, h3);
		assertEquals(20, h2);
		assertEquals(30, h1);
	}
	
	@Test
	public void StreamOfIntegersAppendingElements() {
		
		// creamos un stream usando elementos constantes
		// en realidad se lee en orden inverso 30 :: 20 :: 10 :: empty
		Stream<Integer> s = Stream._.<Integer>empty()
				.append(10)
				.append(20)
				.append(30);
		
		// no puede ser nulo
		assertNotNull(s);
		
		int h1 = s.getHead();
		int h2 = s.getTail().getHead();
		int h3 = s.getTail().getTail().getHead();
		Stream<Integer> h4 = s.getTail().getTail().getTail();
		
		// empezamos confirmando que el último debe ser vacío
		assertTrue(h4.isEmpty());
		
		// y que el resto aparecen en el mismo orden en que se introdujeron
		assertEquals(10, h1);
		assertEquals(20, h2);
		assertEquals(30, h3);
	}
	
	@Test
	public void StreamOfIntegerExists() 
	{
		Stream<Integer> s = Stream._.of(10,20,30);

		// probamos el método exists
		assertTrue(s.exists(Predicate.Int.eq(20)));
		assertTrue(s.exists(Predicate.Int.gt(25)));
		assertFalse(s.exists(Predicate.Int.lt(10)));
	}

	@Test
	public void StreamOfIntegerAppendStream() 
	{
		Stream<Integer> s = Stream._.of(10,20,30)
				.append(Stream._.of(60,80,100))
				.append(Stream._.of(120,200));

		// confirmamos que existen algunos de los elementos
		assertTrue(s.exists(Predicate.Int.eq(20)));
		assertFalse(s.exists(Predicate.Int.eq(25)));
		assertTrue(s.exists(Predicate.Int.eq(80)));
		assertTrue(s.exists(Predicate.Int.eq(120)));
		assertFalse(s.exists(Predicate.Int.eq(90)));
		assertFalse(s.exists(Predicate.Int.eq(230)));
	}

	@Test
	public void StreamOfIntegerSkip() 
	{
		Stream<Integer> s = Stream._.of(10,20,30,40,50,60,70,80,90,100);
		
		// quitamos los tres primeros
		Stream<Integer> ss = s.skip(3);
		
		// confirmamos que los primeros no deben existir
		assertFalse(ss.exists(Predicate.Int.eq(10)));
		assertFalse(ss.exists(Predicate.Int.eq(20)));
		assertFalse(ss.exists(Predicate.Int.eq(30)));
		
		// pero que los siguientes sí
		assertTrue(ss.exists(Predicate.Int.eq(40)));
		assertTrue(ss.exists(Predicate.Int.eq(50)));
		assertTrue(ss.exists(Predicate.Int.eq(60)));
		assertTrue(ss.exists(Predicate.Int.eq(70)));
		assertTrue(ss.exists(Predicate.Int.eq(80)));
		assertTrue(ss.exists(Predicate.Int.eq(90)));
		assertTrue(ss.exists(Predicate.Int.eq(100)));
		
		// de hecho, la cabeza debe ser ahora 40, mientras que la cabeza de la anterior sigue siendo 10
		assertEquals((Integer)10, s.getHead());
		assertEquals((Integer)40, ss.getHead());
	}

	@Test
	public void StreamOfIntegerForAll() {
		Stream<Integer> s = Stream._.intRange(1);
		
		assertTrue(
				"Pues resulta que no todos son par !!",
				s.map(IntFunction.identity().multiply(2))  // todos por dos (para que sean pares)
				.skip(1000)
				.take(1000)
				.forAll(Predicate.Int.isPar()));
		
		assertTrue(
				"Pues resulta que no todos son impares !!",
				s.map(IntFunction.identity().multiply(2).add(-1)) // todo por dos y restamos 1
				.skip(101)
				.take(500)
				.forAll(Predicate.Int.isOdd()));

		// en estado normal no pueden ser todos pares o impares
		assertFalse(s.forAll(Predicate.Int.isPar()));
	}
	
	@Test
	public void StreamOfIntegerTake() 
	{
		Stream<Integer> s = Stream._.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160);
		
		// quitamos los tres primeros
		Stream<Integer> ss = s.take(5);

		// confirmamos que en la primera existe el elemento 60 y el 120
		assertTrue(s.exists(Predicate.Int.eq(60)));
		assertTrue(s.exists(Predicate.Int.eq(120)));

		// y que en la segunda no
		assertFalse(ss.exists(Predicate.Int.eq(60)));
		assertFalse(ss.exists(Predicate.Int.eq(120)));
		
		// pero que sí existen los cinco primeros
		assertTrue(ss.exists(Predicate.Int.eq(10)));
		assertTrue(ss.exists(Predicate.Int.eq(20)));
		assertTrue(ss.exists(Predicate.Int.eq(30)));
		assertTrue(ss.exists(Predicate.Int.eq(40)));
		assertTrue(ss.exists(Predicate.Int.eq(50)));
	}

	
	@Test
	public void StreamOfIntegerMap() 
	{
		Stream<Integer> s = Stream._.of(10,20,30);

		Stream<String> ss = s.map(new Function<Integer, String>() {
			@Override public String apply(Integer v) { return v.toString(); }
		});
		
		// probamos el método exists
		assertTrue(ss.exists(Predicate.Str.eq("20")));
		assertFalse(ss.exists(Predicate.Str.eq("25")));
		assertTrue(ss.exists(Predicate.Str.eq("30")));
	}

	@Test
	public void StreamRangeOfInts() {
		
		// rango entre 200 y 1000 con saltos de 7, pero ignorando los 11 primeros
		Stream<Integer> r = Stream._.intRange(200, 1000, 7)
				.skip(11);
		
		// no debe estar el 200 (obvio) ni el 270.
		assertFalse(r.exists(Predicate.Int.eq(200)));
		assertFalse(r.exists(Predicate.Int.eq(270)));
		
		// sí debe estar el siguiente, 277
		assertTrue(r.exists(Predicate.Int.eq(277)));
	}
	
	/**
	 * Test complejo que muestra la posibilidad de Stream en casos que se requieren operaciones 
	 * diferentes y filtrados variables.
	 */
	@Test
	public void StreamRangeOfIntsComplex() {
		
		// multiplica un elemento por 13
		// y devolvemos una tupla con dos elementos.
		Function<Integer,Tuples.T2<Integer, String>> _map_x_13 = IntFunction.identity()
				.multiply(13)
				.andThen(new Function<Integer,Tuples.T2<Integer, String>>() {
					@Override public T2<Integer, String> apply(Integer v) { return Tuples.tuple(v, v.toString()); }
				});

		// nos quedamos solamente con el primer elemento
		// y lo convertimos en su versión original dividiendo entre 13
		Function<Tuples.T2<Integer, String>, Integer> _map_tuple_2_int = 
				new Function._<Tuples.T2<Integer, String>, Integer>() {
					@Override public Integer apply(T2<Integer, String> v) {
						return v._1;
				}
			}
		.andThen(IntFunction.identity().divide(13));

		// aquellos elementos que tengan en su interior la subcadena "31"
		// para lo que necesitamos quedarnos solo con la parte de la cadena.
		Predicate<Tuples.T2<Integer,String>> _p_tiene31 = Predicate.Str.contains(
				new Function<Tuples.T2<Integer, String>, String>() {
					@Override public String apply(T2<Integer, String> v) { return v._2; }
				}, "31"); 
			
		// los elementos que son divisibles entre 161 (23 * 7)
		Predicate<Integer> _p_divisible161 = new Predicate<Integer>() {
			@Override public boolean test(Integer v) { return v % 161 == 0; }
		};
		
		// ---

		// creamos un rango de muchos elementos (casi infinito)
		Stream<Integer> range = Stream._.intRange(1,Integer.MAX_VALUE,1);
		
		// ¿qué números enteros, multiplicados por 13, contienen un 31 como parte de su subsecuencia?
		// de haberlos ignoramos los veinte primeros y nos quedamos con los diez siguientes que cumplen que
		// son divisibles entre 161.
		Stream<Integer> mapped = range
				.map(_map_x_13)
				.filter(_p_tiene31)
				.map(_map_tuple_2_int)
				.skip(20)
				.filter(_p_divisible161)
				.take(10);

		// de momento no aseveramos nada, simplemente pintamos
		for(Integer e: mapped) System.out.println(e);
	}

	/**
	 * Con esta prueba quiero asegurar que se puede añadir cualquier stream siempre que sea de un subtipo del original.
	 */
	@Test
	public void ConcatenateStreamsOfSubtypes() {
		
		Stream<Number> sn = Stream._.<Number>empty()
				.append(Stream._.of(10,20,30,40,105,45))
				.append(Stream._.of(10.0, 22.5, 95.3))
				.append(Stream._.of(-1L, 77L, 102L))
				.append(Stream._.of(108.13f, 66.44f, -201.4f));
		
		assertTrue(sn.exists(new Predicate<Number>() {
			@Override public boolean test(Number v) { return v.doubleValue() > 100.0; }
		}));
	}
	
	@Test
	public void StreamOfIntsFoldLeft() {
		
		Stream<Integer> s = Stream._.intRange(1,100)
				.skip(50)
				.take(5);
		
		// calculamos usando foldLeft
		int acc = s.foldLeft(0, new BiFunction<Integer,Integer,Integer>() {
			@Override public Integer apply(Integer acc, Integer val) { return acc + val; }
		});
		
		// calculamos con un bucle
		int acc_b = 0;
		for(Integer e: s) { acc_b = acc_b + e; }
		
		assertEquals(acc_b, acc);
	}
	
	@Test
	public void StreamOfLongsFoldLeftToCalculateFibonacci() {

		// calcularemos el número Fibonacci de la posición 50 
		
		long r = Stream._.repeat(1L) // infinito 
				.take(48)  // necesitamos 48 para la función de acumulación pues ya hay dos precalculados
				.foldLeft(Tuples.tuple(1L, 1L), 
						new BiFunction<Tuples.T2<Long, Long>, Long, Tuples.T2<Long, Long>>() {

							// acumulamos a lo Fibonnacci
							@Override public Tuples.T2<Long, Long> apply(
									Tuples.T2<Long, Long> t, Long v) {
								// el valor de v lo ignoramos, porque no lo necesitamos
								return Tuples.tuple(t._2, t._1+t._2);
							}
				})._2;
		
		assertEquals(12586269025L, r);
		
		System.out.printf("Valor posición 50 Fibonacci: %d", r);
	}
	
	// preexistente en FFTest
	@Test
	public void StreamOfStringsGetEnumeration() {
		
		Collection<Integer> ll = Collections.list(
				Stream._.of("hola", "Caracola", "hola", "caracol")
					.filter(Predicate.Str.pattern("[Cc].*"))
					.map( new Function<String, Integer>() { @Override public Integer apply(String v) { return v.length(); }})
					.filter(Predicate.Int.gt(5))
					.getEnumeration()
				);

		assertArrayEquals(new Integer[]{8, 7}, ll.toArray());
	}
	
	@Test
	public void StreamOfIntsTakeWhile() {
	}
}
