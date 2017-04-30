package es.nimio.nimiogcs.functional;

import java.io.Serializable;

public interface Optional<A> extends Serializable {
  
	public enum Tag {
		NONE,
		SOME
	}
	
	Tag getTag();
	
	/**
	 * Transforma un tipo opcional en otro tipo opcional.
	 * Solo si el de origen es distinto de None.
	 * @param f
	 * @return
	 */
	<B> Optional<B> map (Function<? super A, ? extends B> f);
	
	/**
	 * La función entregada es capaz de devolver un valor Option<B>
	 * @param f
	 * @return
	 */
	<B> Optional<B> flatMap(Function<? super A, Optional<B>> f);
	
	/**
	 * B debe ser un supertipo de A.
	 * @param deflt
	 * @return
	 */
	<B> B getOrElse(Lazy<B> deflt);
	
	/**
	 * B debe ser un supertipo de A.
	 * @param ob
	 * @return
	 */
	<B> Optional<B> orElse(Lazy<Optional<B>> ob);
	
	/**
	 * Convierte Some en None si no se satistafe el predicado.
	 * @param p
	 * @return
	 */
	Optional<A> filter(Predicate<? super A> p);
	
	/**
	 * Indica cuándo un registro opcional es none
	 * @return
	 */
	boolean isNone();
	
	/**
	 * Devuelve el valor interno.
	 */
	A getValue();
	
	
	// ----------------------------------------
	
	public final static class _ {
		
		public final static None<?> NONE = new None<Object>(); 
		
		public static <A> Optional<A> None() {
			return new None<A>();
		}
		
		public static <A> Optional<A> of(A value) {
			if(value != null) return new Some<A>(value);
			else return new None<A>();
		}
		
		/**
		 * Lift de una función que opera sobre un único tipo (operador)
		 * @param f
		 * @return
		 */
		public static <A> Function<Optional<A>,Optional<A>> liftOp(final Function<? super A, ? extends A> f) {
			return new Function<Optional<A>, Optional<A>> () {
				@Override 
				public Optional<A> apply(Optional<A> value) {
					return value.map(f);
				}
			};
		}

		/**
		 * Permite que, partiendo de tipos Option, se puedan utilizar funciones sin el tipo Option.
		 * @param f
		 * @return
		 */
		public static <A,B> Function<Optional<A>,Optional<B>> lift(final Function<? super A, ? extends B> f) {
			return new Function<Optional<A>, Optional<B>> () {
				@Override 
				public Optional<B> apply(Optional<A> value) {
					return value.map(f);
				}
			};
		}
	}
	
	/**
	 * Versión sin dato de Option.
	 *
	 * @param <A>
	 */
	final static class None<A> implements Optional<A> {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -7244909015940862575L;
		
		protected None() {} 

		@Override
		public es.nimio.nimiogcs.functional.Optional.Tag getTag() {
			return Tag.NONE;
		}
		
		@Override
		public <B> Optional<B> map(Function<? super A, ? extends B> f) {
			return new None<B>();
		}

		@Override
		public <B> Optional<B> flatMap(Function<? super A, Optional<B>> f) {
			return new None<B>();
		}

		@Override
		public <B> B getOrElse(Lazy<B> deflt) {
			return deflt.eval();
		}

		@Override
		public <B> Optional<B> orElse(Lazy<Optional<B>> ob) {
			return ob.eval();
		}

		@Override
		public Optional<A> filter(Predicate<? super A> p) {
			return this;
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			return true;
		}
		
		@Override public boolean isNone() { return true; }
		@Override public A getValue() { return null; }
	}
	
	final static class Some<A> implements Optional<A> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 167277719450563277L;
		
		private final A _value;
		
		protected Some(A value) { _value = value; }
		
		@Override
		public es.nimio.nimiogcs.functional.Optional.Tag getTag() {
			return Tag.SOME;
		}
		
		@Override
		public <B> Optional<B> map(Function<? super A, ? extends B> f) {
			return _.<B>of(f.apply(_value) );
		}

		@Override
		public <B> Optional<B> flatMap(Function<? super A, Optional<B>> f) {
			try {
				Optional<B> b = (Optional<B>) f.apply(_value);
				return b;
			} catch (Throwable t) {
				return new None<B>();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public <B> B getOrElse(Lazy<B> deflt) {
			return (B) _value;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <B> Optional<B> orElse(Lazy<Optional<B>> ob) {
			return new Some<B>((B)_value);
		}

		@Override
		public Optional<A> filter(Predicate<? super A> p) {
			if (!p.test(_value)) return new None<A>();
			else return this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((_value == null) ? 0 : _value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Some<?> other = (Some<?>) obj;
			if (_value == null) {
				if (other._value != null)
					return false;
			} else if (!_value.equals(other._value))
				return false;
			return true;
		}
		
		@Override public boolean isNone() { return false; }
		@Override public A getValue() { return _value; }
	}
	
}
