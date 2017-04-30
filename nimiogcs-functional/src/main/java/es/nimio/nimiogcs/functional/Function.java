package es.nimio.nimiogcs.functional;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

public interface Function<T,R> 
{
	R apply(T v);

	/**
	 * Implementación base extendida que incluye varias de los métodos que ofrece 
	 * la interfaz Funcion de Java 8 y otros mecanismos para poder instanciar
	 * una función. 
	 *
	 * @param <T>
	 * @param <R>
	 */
	public abstract class _<T,R> implements Function<T,R> {

		public <V> Function._<T, V> andThen(final Function<? super R, ? extends V> after) {
			final Function<T,R> _t = this;
			return new _<T,V> () {
				@Override public V apply(T v) { return after.apply(_t.apply(v)); }
			};
		}
		
		public <V> Function._<V, R> compose(final Function<? super V, ? extends T> before) {
			final Function<T,R> _t = this;
			return new _<V,R> () {
				@Override public R apply(V v) { return _t.apply(before.apply(v)); }
			};
		}
		
		// utilidad

		// identity no se puede definir de forma estática en una interfaz

		public static <A> Function._<A,A> identity() {
			return new _<A,A> () {
				@Override public A apply(A v) { return v; }
			};
		}
		
		// en Java 8 sustituir por of(java.util.function.Function<A,B> f)
		
		public static <A,B> _<A,B> of(final Function<A,B> f) {
			return new _<A, B>() {
				@Override public B apply(A v) { return f.apply(v); }
			};
		}
		
		/**
		 * Define una función a partir de un dominio (claves) contenido en un mapa.
		 * 
		 * @param dominio - Dominio de la función.
		 * @param def - Valor por defecto cuando no esté definida la función.
		 * @return
		 */
		public static <A,B> Function<A,B> of(final Map<A,B> dominio, final B def) {
			return new Function<A,B> () {

				@Override
				public B apply(A v) {
					// si no existe la clave devolvemos el valor por defecto
					if(!dominio.containsKey(v)) return def;
					// en caso contrario devolemos el susodicho valor.
					else return dominio.get(v);
				}
				
			};
		}
		
		@SuppressWarnings("unchecked")
		public static <A, B> _<A, B> ofGtter(final String property) {
		    return new _<A, B>() {
		        public B apply(A bean) {
		            try {
		                Method getter = 
		                    new PropertyDescriptor(property, bean.getClass()).getReadMethod();
		                return (B) getter.invoke(bean);
		            } catch (Exception e) {
		                return null;
		            }
		        }
		    };
		}
		
		@SuppressWarnings("unchecked")
		public static <A, B> _<A, B> ofMethod(final String methodName) {
		    return new _<A, B>() {
		        public B apply(A bean) {
		            try {
		                Method getter =
		                	bean.getClass().getMethod(methodName, (Class<?>[]) null);
		                return (B) getter.invoke(bean);
		            } catch (Exception e) {
		                return null;
		            }
		        }
		    };
		}
	}
}
