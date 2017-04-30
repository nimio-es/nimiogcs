package es.nimio.nimiogcs.functional;

import org.junit.Test;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Optional;

import static org.junit.Assert.*;

public class OptionalTest {

	@Test
	public void TwoNoneOfSameTypeAreEqual() {
		
		Optional<Integer> noneA = Optional._.of(null);
		Optional<Integer> noneB = Optional._.of(null);
		
		// los dos deben considerarse iguales
		assertTrue(noneA.equals(noneB));
	}
	
	@Test
	public void TwoNoneOfDifferentTypeAreEqualAlso() {

		Optional<Integer> noneA = Optional._.of(null);
		Optional<String> noneB = Optional._.of(null);

		// los dos deben considerarse iguales
		assertTrue(noneA.equals(noneB));

		// incluso cuando ambos tipos forman una jerarquía
		Optional<Number> noneC = Optional._.of(null);
		assertTrue(noneA.equals(noneC));
	}

	@Test
	public void TwoSomeOfSameTypeAndSameValueAreEqual() {
		
		Optional<Integer> someA = Optional._.of(32);
		Optional<Integer> someB = Optional._.of(32);
		
		// dos instancias distintas
		assertTrue(someA != someB);
		
		// pero considerables iguales
		assertTrue(someA.equals(someB));
	}

	@Test
	public void TwoSomeOfSameTypeButDiffValueAreDiff() {
		
		Optional<Integer> someA = Optional._.of(31);
		Optional<Integer> someB = Optional._.of(32);

		// mismo tipo pero distinto valor
		assertFalse(someA.equals(someB));
	}

	@Test
	public void TwoSomeOfDiffTypeButSameValueMaybeEquals() {
		
		// Hay que tener cuidado con esto, pues lo que se almacena 
		// es el tipo de valor y éste puede ser del mismo tipo
		// por lo que el resultado puede ser que sean dos Some iguales
		
		Optional<Integer> someA = Optional._.of(32);
		Optional<Number> someB = Optional._.<Number>of(32);
		
		// en este caso se pueden considerar iguales
		assertTrue(someA.equals(someB));
		
		Optional<Number> someC = Optional._.<Number>of(32L);
		
		// pero en este caso no
		assertFalse(someA.equals(someC));
	}

	
	@Test
	public void SomeAndNoneAreDifferentEver() {
		Optional<Integer> noneA = Optional._.of(null);
		Optional<Integer> someB = Optional._.of(32);

		// no pueden considerarse iguales en ningún orden de comparación.
		assertFalse(noneA.equals(someB));
		assertFalse(someB.equals(noneA));
	}
	
	@Test
	public void MapNoneDoesNone() {
		
		Optional<Integer> noneA = Optional._.of(null);
		Optional<String> noneB = noneA.map(new Function<Number,String>() {
			@Override public String apply(Number v) { return v.toString(); }
		});
		
		assertEquals(noneA, noneB);
	}

	@Test
	public void FlatMapOk() {
		Optional<Integer> someA = Optional._.of(32);
		Optional<String> someB = someA.flatMap(new Function<Integer, Optional<String>>() {
			public Optional<String> apply(Integer v) { return Optional._.of(v.toString()); };
		});
		
		// confirmar que el valor devuelto es 0
		assertEquals(Optional._.of("32"), someB);
	}
	
	@Test
	public void FlatMapFailsDoesNone() {
		Optional<Integer> someA = Optional._.of(32);
		Optional<String> someB = someA.flatMap(new Function<Integer, Optional<String>>() {
			public Optional<String> apply(Integer v) { Integer res = v / 0; return Optional._.of(res.toString()); };
		});
		
		// confirmar que el valor devuelto es 0
		assertEquals(Optional._.NONE, someB);
	}
	
	
}
