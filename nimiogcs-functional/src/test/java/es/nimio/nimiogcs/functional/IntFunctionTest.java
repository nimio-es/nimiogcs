package es.nimio.nimiogcs.functional;

import org.junit.Test;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.IntFunction;

import org.junit.Assert;

public class IntFunctionTest {

	@Test
	public void integerFunctionXDiv2Test() {
		
		// x / 2
		Function<Integer, Integer> f = IntFunction.identity().divide(2);
		
		Assert.assertEquals((Integer)5, f.apply(10));
		Assert.assertEquals((Integer)47, f.apply(95));
	}

	@Test
	public void integerFunctionXMultXTest() {
		
		// x^2
		Function<Integer, Integer> f = IntFunction.identity().multiply(IntFunction.identity());
		
		Assert.assertEquals((Integer)25, f.apply(5));
		Assert.assertEquals((Integer)9, f.apply(3));
	}
	
}
