package es.nimio.nimiogcs.functional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.IntFunction;
import es.nimio.nimiogcs.functional.Predicate;
import es.nimio.nimiogcs.functional.Predicate.Composer;

public class PredicateTest {

	@Test
	public void orTest() {
		Predicate._<Integer> gt42 = Predicate.Int.gt(IntFunction.identity(), 42);
		Predicate<Integer> lt42 = gt42.negate();
		
		// 50 debe ser o mayor que 42 o menor que 42
		assertTrue(gt42.or(lt42).test(50));
	}
	
	interface Persona {
		int getEdad();
		int getHijos();
		boolean isCasado();
	}

	Persona persona = new Persona() {
		@Override public int getEdad() { return 50; }
		@Override public int getHijos() { return 3; }
		@Override public boolean isCasado() { return false; }
	};
	
	@Test
	public void orTestWithType() {
		
		Predicate<Persona> gt42 = Predicate.Int.gt(Function._.<Persona,Integer>ofMethod("getEdad"),42);
		Predicate<Persona> le42 = Predicate.Composer.negate(gt42);
		
		// 50 debe ser o mayor que 42 o menor que 42
		assertTrue(gt42.test(persona));
		assertFalse(le42.test(persona));
		assertTrue(Composer.or(gt42,le42).test(persona));
	}

	@Test
	public void andTestWithType() {
		
		Predicate<Persona> mayor42 = Predicate.Int.gt(Function._.<Persona,Integer>ofMethod("getEdad"),42);
		Predicate<Persona> casado = Predicate.Bool.isTrue(Function._.<Persona, Boolean>ofMethod("isCasado"));
		Predicate<Persona> hijos3oMenos = Predicate.Int.le(Function._.<Persona,Integer>ofMethod("getHijos"),42);
		
		assertTrue(Composer.and(mayor42,hijos3oMenos).test(persona));
		assertFalse(Composer.and(mayor42,casado).test(persona));
		assertTrue(Composer.and(mayor42,Composer.negate(casado)).test(persona));
	}
		
	public static class PersonaBean implements Persona{

		int edad = 50; 
		int hijos = 3;
		boolean casado = false;
		
		@Override
		public int getEdad() {
			return edad;
		}

		@Override
		public int getHijos() {
			return hijos;
		}

		@Override
		public boolean isCasado() {
			return casado;
		}
		
	}
	
}
