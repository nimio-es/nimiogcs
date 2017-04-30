package es.nimio.nimiogcs.functional;

import org.junit.Test;

import es.nimio.nimiogcs.functional.Function;
import es.nimio.nimiogcs.functional.Lazy;

import static org.junit.Assert.*;

public class LazyTest {

	// devuelve el parámetro que se use en su construcción.
	final class MiLazyEval<T> implements Lazy<T> {
		
		private int evals = 0;
		private T value;
		
		public int getEvals() { return evals; }

		
		public MiLazyEval(T value) {
			this.value = value;
		}
		
		@Override
		public T eval() {
			evals++;
			return this.value;
		}
	}
	
	@Test
	public void reEvalTest() {
		
		Lazy<Integer> lazy = new MiLazyEval<Integer>(10);
		
		// confirmar que no cambia la respuesta por más veces que se le llame
		assertEquals((Integer)10, lazy.eval());
		assertEquals((Integer)10, lazy.eval());
		assertEquals((Integer)10, lazy.eval());

		// confirmar que la evluación se ha realizado ese número de veces
		assertEquals(3, ((MiLazyEval<Integer>)lazy).getEvals());
	}
	
	@Test
	public void evalOneTimeWithMemoizeTest() {
		
		Lazy<Integer> lazy = new Lazy.Memoize<Integer>(new MiLazyEval<Integer>(10));
		
		// confirmar que no cambia la respuesta por más veces que se le llame
		assertEquals((Integer)10, lazy.eval());
		assertEquals((Integer)10, lazy.eval());
		assertEquals((Integer)10, lazy.eval());

		// confirmar que la evaluación se realiza únicamente una vez
		assertEquals(1, ((MiLazyEval<Integer>)((Lazy.Memoize<Integer>)lazy).getLazy()).getEvals());		
	}
	
	@Test
	public void heavyFunctionLazyEvaluationTest() {

		// función muy pesada
		final Function<Integer,Integer> f = new Function<Integer, Integer> () {

			@Override
			public Integer apply(Integer v) {
				
				System.out.println("Antes de ejecutar la operación pesada.");

				// ****** OPERACION PESADA *********
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				
				System.out.println("Después de ejecutar la operación pesada.");

				return v;
			}
		};

		// la evaluación perezosa
		Lazy<Integer> lazyF = new Lazy.LazyFunctionEval<Integer,Integer>(f, 10);

		assertEquals((Integer)10, lazyF.eval());
		assertEquals((Integer)10, lazyF.eval());
		assertEquals((Integer)10, lazyF.eval());
	}
}
