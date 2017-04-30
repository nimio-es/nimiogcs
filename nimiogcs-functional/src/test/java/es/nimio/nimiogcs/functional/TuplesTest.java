package es.nimio.nimiogcs.functional;

import org.junit.Test;

import es.nimio.nimiogcs.functional.Tuples;

import static org.junit.Assert.*;

public class TuplesTest {

	// ---- T2 ----------------
	
	@Test
	public void T2_ValuesOfElementsAreCorrect() {

		// creamos la tupla
		Tuples.T2<Integer, String> t1 = Tuples.tuple(2, "que");
		
		// comprobamos los valores
		assertEquals((Integer)2, t1._1);
		assertEquals("que", t1._2);
	}
	
	@Test
	public void T2_DifferentTuplesWithSameValuesAreEquals() {
		
		// creamos la primera tupla
		Tuples.T2<Integer, String> t1 = Tuples.tuple(2, "que");
		
		// creamos la segunda con exactamente los mismos datos
		Tuples.T2<Integer, String> t2 = Tuples.tuple(2, "que");
		
		assertFalse(t1 == t2);
		assertTrue(t1.equals(t2));
	}
	
	@Test
	public void T2_OneTupleObtainedFromOtherAreDifferentObject_WithFirst() {
		
		// creamos la primera tupla
		Tuples.T2<Integer, String> t1 = Tuples.tuple(2, "que");
		
		// obtenemos la segunda cambiando el primer valor
		Tuples.T2<Integer, String> tm = t1.withFirst(100);
		
		// comprobamos que no son la misma instancia y que son distintos
		assertFalse(t1 == tm);
		assertFalse(t1.equals(tm));
		
		// que el valor de la primera queda inmutable
		assertEquals((Integer)2, t1._1);
		
		// además que los valores de la segunda son los esperados
		assertEquals((Integer)100, tm._1);
		assertEquals("que", tm._2);	
	}
	
	@Test
	public void T2_OneTupleObtainedFromOtherAreDifferentObject_WithSecond() {
		
		// creamos la primera tupla
		Tuples.T2<Integer, String> t1 = Tuples.tuple(2, "que");
		
		// obtenemos la segunda cambiando el primer valor
		Tuples.T2<Integer, String> tm = t1.withSecond("para");
		
		// comprobamos que no son la misma instancia y que son distintos
		assertFalse(t1 == tm);
		assertFalse(t1.equals(tm));
		
		// que el valor de la primera queda inmutable
		assertEquals("que", t1._2);
		
		// además que los valores de la segunda son los esperados
		assertEquals((Integer)2, tm._1);
		assertEquals("para", tm._2);	
	}


	// ---- T3 ----------------
	
	@Test
	public void T3_ValuesOfElementsAreCorrect() {

		// creamos la tupla
		Tuples.T3<Integer, String, Double> t1 = Tuples.tuple(2, "que", 99.9);
		
		// comprobamos los valores
		assertEquals((Integer)2, t1._1);
		assertEquals("que", t1._2);
		assertEquals((Double)99.9, t1._3); 
	}
	
	@Test
	public void T3_DifferentTuplesWithSameValuesAreEquals() {
		
		// creamos la primera tupla
		Tuples.T3<Integer, String, Double> t1 = Tuples.tuple(2, "que", 99.9);
		
		// creamos la segunda con exactamente los mismos datos
		Tuples.T3<Integer, String, Double> t2 = Tuples.tuple(2, "que", 99.9);
		
		assertFalse(t1 == t2);
		assertTrue(t1.equals(t2));
	}
	
	@Test
	public void T3_OneTupleObtainedFromOtherAreDifferentObject_WithFirst() {
		
		// creamos la primera tupla
		Tuples.T3<Integer, String, Double> t1 = Tuples.tuple(2, "que", 99.9);
		
		// obtenemos la segunda cambiando el primer valor
		Tuples.T3<Integer, String, Double> tm = t1.withFirst(100);
		
		// comprobamos que no son la misma instancia y que son distintos
		assertFalse(t1 == tm);
		assertFalse(t1.equals(tm));
		
		// que el valor de la primera queda inmutable
		assertEquals((Integer)2, t1._1);
		
		// además que los valores de la segunda son los esperados
		assertEquals((Integer)100, tm._1);
		assertEquals("que", tm._2);	
		assertEquals((Double)99.9, tm._3); 
	}
	
	@Test
	public void T3_OneTupleObtainedFromOtherAreDifferentObject_WithSecond() {
		
		// creamos la primera tupla
		Tuples.T3<Integer, String, Double> t1 = Tuples.tuple(2, "que", 99.9);
		
		// obtenemos la segunda cambiando el primer valor
		Tuples.T3<Integer, String, Double> tm = t1.withSecond("para");
		
		// comprobamos que no son la misma instancia y que son distintos
		assertFalse(t1 == tm);
		assertFalse(t1.equals(tm));
		
		// que el valor de la primera queda inmutable
		assertEquals("que", t1._2);
		
		// además que los valores de la segunda son los esperados
		assertEquals((Integer)2, tm._1);
		assertEquals("para", tm._2);	
		assertEquals((Double)99.9, tm._3); 
	}

	@Test
	public void T3_OneTupleObtainedFromOtherAreDifferentObject_WithThird() {
		
		// creamos la primera tupla
		Tuples.T3<Integer, String, Double> t1 = Tuples.tuple(2, "que", 99.9);
		
		// obtenemos la segunda cambiando el primer valor
		Tuples.T3<Integer, String, Double> tm = t1.withThird(500.60);
		
		// comprobamos que no son la misma instancia y que son distintos
		assertFalse(t1 == tm);
		assertFalse(t1.equals(tm));
		
		// que el valor de la primera queda inmutable
		assertEquals((Double)99.9, t1._3);
		
		// además que los valores de la segunda son los esperados
		assertEquals((Integer)2, tm._1);
		assertEquals("que", tm._2);	
		assertEquals((Double)500.60, tm._3); 
	}


	// ---- T4 ----------------
	
	@Test
	public void T4_ValuesOfElementsAreCorrect() {

		// creamos la tupla
		Tuples.T4<Integer, String, Double, String> t1 = Tuples.tuple(2, "que", 99.9, "siempre");
		
		// comprobamos los valores
		assertEquals((Integer)2, t1._1);
		assertEquals("que", t1._2);
		assertEquals((Double)99.9, t1._3); 
		assertEquals("siempre", t1._4);
	}
	
	@Test
	public void T4_DifferentTuplesWithSameValuesAreEquals() {
		
		// creamos la primera tupla
		Tuples.T4<Integer, String, Double, String> t1 = Tuples.tuple(2, "que", 99.9, "siempre");
		
		// creamos la segunda con exactamente los mismos datos
		Tuples.T4<Integer, String, Double, String> t2 = Tuples.tuple(2, "que", 99.9, "siempre");
		
		assertFalse(t1 == t2);
		assertTrue(t1.equals(t2));
	}
	
	@Test
	public void T4_OneTupleObtainedFromOtherAreDifferentObject_WithFirst() {
		
		// creamos la primera tupla
		Tuples.T4<Integer, String, Double, String> t1 = Tuples.tuple(2, "que", 99.9, "siempre");
		
		// obtenemos la segunda cambiando el primer valor
		Tuples.T4<Integer, String, Double, String> tm = t1.withFirst(100);
		
		// comprobamos que no son la misma instancia y que son distintos
		assertFalse(t1 == tm);
		assertFalse(t1.equals(tm));
		
		// que el valor de la primera queda inmutable
		assertEquals((Integer)2, t1._1);
		
		// además que los valores de la segunda son los esperados
		assertEquals((Integer)100, tm._1);
		assertEquals("que", tm._2);	
		assertEquals((Double)99.9, tm._3); 
		assertEquals("siempre", tm._4);
	}
	
	@Test
	public void T4_OneTupleObtainedFromOtherAreDifferentObject_WithSecond() {
		
		// creamos la primera tupla
		Tuples.T4<Integer, String, Double, String> t1 = Tuples.tuple(2, "que", 99.9, "siempre");
		
		// obtenemos la segunda cambiando el primer valor
		Tuples.T4<Integer, String, Double, String> tm = t1.withSecond("para");
		
		// comprobamos que no son la misma instancia y que son distintos
		assertFalse(t1 == tm);
		assertFalse(t1.equals(tm));
		
		// que el valor de la primera queda inmutable
		assertEquals("que", t1._2);
		
		// además que los valores de la segunda son los esperados
		assertEquals((Integer)2, tm._1);
		assertEquals("para", tm._2);	
		assertEquals((Double)99.9, tm._3); 
		assertEquals("siempre", tm._4);
	}

	@Test
	public void T4_OneTupleObtainedFromOtherAreDifferentObject_WithThird() {
		
		// creamos la primera tupla
		Tuples.T4<Integer, String, Double, String> t1 = Tuples.tuple(2, "que", 99.9, "siempre");
		
		// obtenemos la segunda cambiando el primer valor
		Tuples.T4<Integer, String, Double, String> tm = t1.withThird(500.60);
		
		// comprobamos que no son la misma instancia y que son distintos
		assertFalse(t1 == tm);
		assertFalse(t1.equals(tm));
		
		// que el valor de la primera queda inmutable
		assertEquals((Double)99.9, t1._3);
		
		// además que los valores de la segunda son los esperados
		assertEquals((Integer)2, tm._1);
		assertEquals("que", tm._2);	
		assertEquals((Double)500.60, tm._3); 
		assertEquals("siempre", tm._4);
	}

	@Test
	public void T4_OneTupleObtainedFromOtherAreDifferentObject_WithFourth() {
		
		// creamos la primera tupla
		Tuples.T4<Integer, String, Double, String> t1 = Tuples.tuple(2, "que", 99.9, "siempre");
		
		// obtenemos la segunda cambiando el primer valor
		Tuples.T4<Integer, String, Double, String> tm = t1.withFourth("nunca");
		
		// comprobamos que no son la misma instancia y que son distintos
		assertFalse(t1 == tm);
		assertFalse(t1.equals(tm));
		
		// que el valor de la primera queda inmutable
		assertEquals("siempre", t1._4);
		
		// además que los valores de la segunda son los esperados
		assertEquals((Integer)2, tm._1);
		assertEquals("que", tm._2);	
		assertEquals((Double)99.9, tm._3); 
		assertEquals("nunca", tm._4);
	}
}
