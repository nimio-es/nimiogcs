package es.nimio.nimiogcs.functional;

import java.util.Iterator;

import es.nimio.nimiogcs.functional.Iterables.AbstractIterator;

public final class Iterate {

	static public <T> Iterables<T> from(Iterable<T> it) {
		return from(it.iterator());
	}

	static public <T> Iterables<T> from(Iterator<T> it) {
		return new FromIterator<T>(it);
	}
	
	static public <T> Iterables<T> from(T[] it) {
		return new ArrayIterator<T>(it);
	}
	
	static class ArrayIterator<T> extends AbstractIterator<T> implements Iterables<T> {

		public ArrayIterator(final T[] array) {
			super(new Iterator<T>(){
				int offset = 0;

				@Override
				public boolean hasNext() {
					return offset < array.length;
				}

				@Override
				public T next() {
					return array[offset++];
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
				
			});
		}

	}
	
	static final class FromIterator<T> extends AbstractIterator<T> {

		public FromIterator(final Iterator<T> it) {
			super(it);
		}
	}

}