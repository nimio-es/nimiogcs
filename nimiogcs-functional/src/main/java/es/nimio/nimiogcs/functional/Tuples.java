package es.nimio.nimiogcs.functional;

import java.io.Serializable;

public final class Tuples {

	private Tuples() {}
	
	/**
	 * Tupla con dos elementos.
	 */
	public static class T2<T,U> implements Serializable {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 8326780913946684017L;
		
		public final T _1;
		public final U _2;
		
		public T2(T t, U u) {
			super();
			_1 = t;
			_2 = u;
		}
		
		@Override
		public String toString() { return String.format("(%s, %s)", _1, _2); }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("rawtypes")
			T2 other = (T2) obj;
			if (_1 == null) {
				if (other._1 != null)
					return false;
			} else if (!_1.equals(other._1))
				return false;
			if (_2 == null) {
				if (other._2 != null)
					return false;
			} else if (!_2.equals(other._2))
				return false;
			return true;
		}

		public T2<T,U> withFirst(T n) { return new T2<T,U>(n, _2); } 
		public T2<T,U> withSecond(U n) { return new T2<T,U>(_1, n); } 
		public <R> T3<T,U,R> toT3With(R r) { return new T3<T,U,R>(_1,_2,r); }
	}

	/**
	 * Alias de una tupla de dos elementos que facilita la construcción de pares nombre-descripción
	 */
	public final static class NombreDescripcion extends T2<String, String> {

		private static final long serialVersionUID = 2866459442193327446L;

		public NombreDescripcion(String nombre, String descripcion) {
			super(nombre, descripcion);
		}

		public String getNombre() { return this._1; }
		
		public String getDescripcion() { return this._2; }
		
		/**
		 * Recubre una tupla como una estructura de nombre y descripción
		 */
		public static NombreDescripcion of(T2<String, String> tupla) {
			return new NombreDescripcion(tupla._1, tupla._2);
		}
	}
	
	
	/**
	 * Tupla con tres elementos.
	 */
	public static class T3<T,U,V> implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3538888482380129801L;
		
		public final T _1;
		public final U _2;
		public final V _3;
		
		public T3(T t, U u, V v) {
			super();
			_1 = t;
			_2 = u;
			_3 = v;
		}
		
		@Override
		public String toString() { return String.format("(%s, %s, %s)", _1, _2, _3); }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
			result = prime * result + ((_3 == null) ? 0 : _3.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("rawtypes")
			T3 other = (T3) obj;
			if (_1 == null) {
				if (other._1 != null)
					return false;
			} else if (!_1.equals(other._1))
				return false;
			if (_2 == null) {
				if (other._2 != null)
					return false;
			} else if (!_2.equals(other._2))
				return false;
			if (_3 == null) {
				if (other._3 != null)
					return false;
			} else if (!_3.equals(other._3))
				return false;
			return true;
		}

		public T3<T,U,V> withFirst(T n) { return new T3<T,U,V>(n, _2, _3); }
		public T3<T,U,V> withSecond(U n) { return new T3<T,U,V>(_1, n, _3); } 
		public T3<T,U,V> withThird(V n) { return new T3<T,U,V>(_1, _2, n); } 

	}
	
	/**
	 * Tupla con cuatro elementos.
	 */
	public static class T4<T,U,V,W> implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -1452077726779806296L;
		
		public final T _1;
		public final U _2;
		public final V _3;
		public final W _4;
		
		public T4(T t, U u, V v, W w) {
			super();
			_1 = t;
			_2 = u;
			_3 = v;
			_4 = w;
		}
		
		@Override
		public String toString() { return String.format("(%s, %s, %s, %s)", _1, _2, _3, _4); }

		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
			result = prime * result + ((_3 == null) ? 0 : _3.hashCode());
			result = prime * result + ((_4 == null) ? 0 : _4.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof T4))
				return false;
			@SuppressWarnings("rawtypes")
			T4 other = (T4) obj;
			if (_1 == null) {
				if (other._1 != null)
					return false;
			} else if (!_1.equals(other._1))
				return false;
			if (_2 == null) {
				if (other._2 != null)
					return false;
			} else if (!_2.equals(other._2))
				return false;
			if (_3 == null) {
				if (other._3 != null)
					return false;
			} else if (!_3.equals(other._3))
				return false;
			if (_4 == null) {
				if (other._4 != null)
					return false;
			} else if (!_4.equals(other._4))
				return false;
			return true;
		}

		public T4<T,U,V,W> withFirst(T n) { return new T4<T,U,V,W>(n, _2, _3, _4); }
		public T4<T,U,V,W> withSecond(U n) { return new T4<T,U,V,W>(_1, n, _3, _4); } 
		public T4<T,U,V,W> withThird(V n) { return new T4<T,U,V,W>(_1, _2, n, _4); } 
		public T4<T,U,V,W> withFourth(W n) { return new T4<T,U,V,W>(_1, _2, _3, n); }
	}
	
	/**
	 * Tupla con cinco elementos.
	 */
	public static class T5<T,U,V,W,X> implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -1452077726779806296L;
		
		public final T _1;
		public final U _2;
		public final V _3;
		public final W _4;
		public final X _5;
		
		public T5(T t, U u, V v, W w, X x) {
			super();
			_1 = t;
			_2 = u;
			_3 = v;
			_4 = w;
			_5 = x;
		}
		
		@Override
		public String toString() { return String.format("(%s, %s, %s, %s, %s)", _1, _2, _3, _4, _5); }

		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
			result = prime * result + ((_3 == null) ? 0 : _3.hashCode());
			result = prime * result + ((_4 == null) ? 0 : _4.hashCode());
			result = prime * result + ((_5 == null) ? 0 : _5.hashCode()); 
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof T5))
				return false;
			@SuppressWarnings("rawtypes")
			T5 other = (T5) obj;
			if (_1 == null) {
				if (other._1 != null)
					return false;
			} else if (!_1.equals(other._1))
				return false;
			if (_2 == null) {
				if (other._2 != null)
					return false;
			} else if (!_2.equals(other._2))
				return false;
			if (_3 == null) {
				if (other._3 != null)
					return false;
			} else if (!_3.equals(other._3))
				return false;
			if (_4 == null) {
				if (other._4 != null)
					return false;
			} else if (!_4.equals(other._4))
				return false;
			if (_5 == null) {
				if (other._5 != null)
					return false;
			} else if (!_5.equals(other._5))
				return false;
			return true;
		}

		public T5<T,U,V,W,X> withFirst(T t) { return new T5<T,U,V,W,X>(t, _2, _3, _4, _5); }
		public T5<T,U,V,W,X> withSecond(U u) { return new T5<T,U,V,W,X>(_1, u, _3, _4, _5); } 
		public T5<T,U,V,W,X> withThird(V v) { return new T5<T,U,V,W,X>(_1, _2, v, _4, _5); } 
		public T5<T,U,V,W,X> withFourth(W w) { return new T5<T,U,V,W,X>(_1, _2, _3, w, _5); }
		public T5<T,U,V,W,X> withFifth(X x) { return new T5<T,U,V,W,X>(_1, _2, _3, _4, x); }
		
		public <A> T6<T,U,V,W,X,A> toT6ending(A a) { return new T6<T,U,V,W,X,A>(_1, _2, _3, _4, _5, a); } 
	}

	/**
	 * Tupla con seis elementos.
	 */
	public static class T6<T,U,V,W,X,Y> implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -1452077726779806296L;
		
		public final T _1;
		public final U _2;
		public final V _3;
		public final W _4;
		public final X _5;
		public final Y _6;
		
		public T6(T t, U u, V v, W w, X x, Y y) {
			super();
			_1 = t;
			_2 = u;
			_3 = v;
			_4 = w;
			_5 = x;
			_6 = y;
		}
		
		@Override
		public String toString() { return String.format("(%s, %s, %s, %s, %s, %s)", _1, _2, _3, _4, _5, _6); }

		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
			result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
			result = prime * result + ((_3 == null) ? 0 : _3.hashCode());
			result = prime * result + ((_4 == null) ? 0 : _4.hashCode());
			result = prime * result + ((_5 == null) ? 0 : _5.hashCode()); 
			result = prime * result + ((_6 == null) ? 0 : _6.hashCode()); 
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof T6))
				return false;
			@SuppressWarnings("rawtypes")
			T6 other = (T6) obj;
			if (_1 == null) {
				if (other._1 != null)
					return false;
			} else if (!_1.equals(other._1))
				return false;
			if (_2 == null) {
				if (other._2 != null)
					return false;
			} else if (!_2.equals(other._2))
				return false;
			if (_3 == null) {
				if (other._3 != null)
					return false;
			} else if (!_3.equals(other._3))
				return false;
			if (_4 == null) {
				if (other._4 != null)
					return false;
			} else if (!_4.equals(other._4))
				return false;
			if (_5 == null) {
				if (other._5 != null)
					return false;
			} else if (!_5.equals(other._5))
				return false;
			if (_6 == null) {
				if (other._6 != null)
					return false;
			} else if (!_6.equals(other._6))
				return false;
			return true;
		}

		public T6<T,U,V,W,X,Y> withFirst(T t) { return new T6<T,U,V,W,X,Y>(t, _2, _3, _4, _5, _6); }
		public T6<T,U,V,W,X,Y> withSecond(U u) { return new T6<T,U,V,W,X,Y>(_1, u, _3, _4, _5, _6); } 
		public T6<T,U,V,W,X,Y> withThird(V v) { return new T6<T,U,V,W,X,Y>(_1, _2, v, _4, _5, _6); } 
		public T6<T,U,V,W,X,Y> withFourth(W w) { return new T6<T,U,V,W,X,Y>(_1, _2, _3, w, _5, _6); }
		public T6<T,U,V,W,X,Y> withFifth(X x) { return new T6<T,U,V,W,X,Y>(_1, _2, _3, _4, x, _6); }
		public T6<T,U,V,W,X,Y> withSixth(Y y) { return new T6<T,U,V,W,X,Y>(_1, _2, _3, _4, _5, y); }
	}

	// ------------------------------------------------------------------------------------
	// Utilidades
	// ------------------------------------------------------------------------------------
	
	/**
	 * Crea una nueva tupla de dos elementos.
	 */
	public static <T,U> T2<T,U> tuple(T t, U u) { return new T2<T, U>(t,u); }
	
	/**
	 * Crea una nueva tupla con tres elementos.
	 */
	public static <T,U,V> T3<T,U,V> tuple(T t, U u, V v) { return new T3<T,U,V>(t,u,v); }

	/**
	 * Crea una nueva tupla con cuatro elementos.
	 */
	public static <T,U,V,W> T4<T,U,V,W> tuple(T t, U u, V v, W w) { return new T4<T,U,V,W>(t,u,v,w); }
	
	/**
	 * Crea una nueva tupla con cinco elementos.
	 */
	public static <T,U,V,W,X> T5<T,U,V,W,X> tuple(T t, U u, V v, W w, X x) { return new T5<T,U,V,W,X>(t,u,v,w,x); }

	/**
	 * Crea una nueva tupla con seis elementos.
	 */
	public static <T,U,V,W,X,Y> T6<T,U,V,W,X,Y> tuple(T t, U u, V v, W w, X x, Y y) { return new T6<T,U,V,W,X,Y>(t,u,v,w,x,y); }
}


