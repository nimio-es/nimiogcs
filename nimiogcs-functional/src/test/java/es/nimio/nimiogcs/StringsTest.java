package es.nimio.nimiogcs;

import static org.junit.Assert.*;

import org.junit.Test;

import es.nimio.nimiogcs.Strings;

public class StringsTest {

	@Test
	public void testRTrim() {
		
		// casos particulares: mal sistema de testing
		assertEquals(Strings.rtrim("SINESPACIOSALADERECHA"), "SINESPACIOSALADERECHA");
		assertEquals(Strings.rtrim("CONESPACIOSALADERECHA       "), "CONESPACIOSALADERECHA");
		assertEquals(Strings.rtrim("       CONESPACIOSALAIZQUIERDAYALADERECHA         "), "       CONESPACIOSALAIZQUIERDAYALADERECHA");
	}

	@Test
	public void testLTrim() {
		
		// casos particulares: mal sistema de testing
		assertEquals(Strings.ltrim("SINESPACIOSALAIZQUIERDA"), "SINESPACIOSALAIZQUIERDA");
		assertEquals(Strings.ltrim("       CONESPACIOSALAIZQUIERDA"), "CONESPACIOSALAIZQUIERDA");
		assertEquals(Strings.ltrim("       CONESPACIOSALAIZQUIERDAYALADERECHA         "), "CONESPACIOSALAIZQUIERDAYALADERECHA         ");
	}
	
	@Test
	public void testPropiedadCombinacionTrims() {

		final String caso0 = "       CONESPACIOSALAIZQUIERDAYALADERECHA         ";
		
		// casos particulares: mal sistema de testing
		assertEquals(
				Strings.rtrim(
						Strings.ltrim(caso0)
				),
				caso0.trim()
		);
	}
}
