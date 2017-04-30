package es.nimio.nimiogcs.functional;

public abstract class IntFunction extends Function._<Integer, Integer> {

	public IntFunction negate () {
		final IntFunction _t = this;
		return new IntFunction () {
			@Override public Integer apply(Integer v) {
				return -_t.apply(v);
			}
		};
	}
	
	public IntFunction add (final Function<Integer, Integer> after) {
		final IntFunction _t = this;
		return new IntFunction () {
			@Override public Integer apply(Integer v) {
				return _t.apply(v) + after.apply(v) ;
			}
		};
	}
	
	public IntFunction add (final Integer e) {
		return add(escalar(e));
	}
	
	
	public IntFunction multiply(final Function<Integer, Integer> after) {
		final IntFunction _t = this;
		return new IntFunction () {
			@Override public Integer apply(Integer v) {
				return _t.apply(v) * after.apply(v) ;
			}
		};
	}
	
	public IntFunction multiply(final Integer e) {
		return multiply(escalar(e));
	}

	public IntFunction divide(final Function<Integer, Integer> after) {
		final IntFunction _t = this;
		return new IntFunction () {
			@Override public Integer apply(Integer v) {
				return _t.apply(v) / after.apply(v) ;
			}
		};
	}
	
	public IntFunction divide(final Integer e) {
		return divide(escalar(e));
	}
	
	/**
	 * Devuelve una función que transforma lo que devuelve la actual 
	 * en una cadena de texto.
	 * @return
	 */
	public Function<Integer, String> toStr() {
		return this.andThen( new Function._<Integer,String>() {
			@Override public String apply(Integer v) { return v.toString(); }
		});
	}
	
	// ...
	
	// ----------------------  

	public static IntFunction escalar(final int e) {
		return new IntFunction() {
			@Override public Integer apply(Integer v) { return e; }
		};
	}

	@SuppressWarnings("unchecked")
	public static IntFunction identity() {
		return new IntFunction() {
			@Override public Integer apply(Integer v) { return v; }
		};
	}
}
