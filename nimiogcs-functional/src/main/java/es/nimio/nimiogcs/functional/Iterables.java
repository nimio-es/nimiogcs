package es.nimio.nimiogcs.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface Iterables<T> extends Iterable<T> {

	<R> Iterables<R> map(Function<T, R> mapper);

	Iterables<T> match(Predicate<T> m);

	void consum(Action<T> m);

	Collection<T> toCollection();

	List<T> toList();

	Set<T> toSet();

	<R> R fold(R initial, BiFunction<T, R, R> mapper);

	abstract static class AbstractIterator<T> implements Iterables<T> {

		private Iterator<T> iterator;

		public AbstractIterator(final Iterator<T> iterator) {
			this.iterator = iterator;
		}

		@Override
		public <R> Iterables<R> map(Function<T, R> m) {
			return new MapIterator<T, R>(iterator(), m);
		}

		@Override
		public Iterables<T> match(Predicate<T> p) {
			return new MatchIterator<T>(iterator(), p);
		}

		@Override
		public Iterator<T> iterator() {
			return iterator;
		}

		@Override
		public void consum(Action<T> c) {
			for (T element : this) {
				c.apply(element);
			}
		}

		@Override
		public final Collection<T> toCollection() {
			return cummulate( new ToList<T>()); //Resolved as List
		}
		@Override
		public final List<T> toList() {
			return cummulate( new ToList<T>());
		}
		@Override
		public final Set<T> toSet() {
			return cummulate( new ToSet<T>());
		}

		@Override
		public final <R> R fold( final R initial,  final BiFunction<T, R, R> mapper) {
			return cummulate(new Fold<T, R>(initial, mapper));
		}
		
		private final <R> R cummulate( final CummulativeAction<R, T> action) {
			this.consum(action);
			return action.get();
		}

	};

	final class MapIterator<T, S> extends AbstractIterator<S> {

		public MapIterator(final Iterator<T> it, final Function<T, S> m) {
			super(new Iterator<S>() {

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public S next() {
					return m.apply(it.next());
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			});
		}
	}

	final class MatchIterator<T> extends AbstractIterator<T> {

		public MatchIterator(final Iterator<T> it, final Predicate<T> p) {
			super(new Iterator<T>() {
				T next = null;

				@Override
				public boolean hasNext() {
					while (next == null && it.hasNext()) {
						final T inner = it.next();
						if (p.test(inner))
							next = inner;
					}
					return next != null;
				}

				@Override
				public T next() {
					final T ret = next;
					next = null;
					return ret;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			});
		}
	}

	final class ToSet<T> extends CummulativeAction<Set<T>,T> {

		public ToSet() {
			super(new HashSet<T>());
		}

		@Override
		public void apply(T t) {
			super.val.add(t);
		}
	}

	final class ToList<T> extends CummulativeAction<List<T>,T> {

		public ToList() {
			super(new ArrayList<T>());
		}

		@Override
		public void apply(T t) {
			super.val.add(t);
		}

	}

	final class Fold<T, R> extends CummulativeAction<R,T> {

		private BiFunction<T, R, R> mapper;

		public Fold(R initial, BiFunction<T, R, R> mapper) {
			super(initial);
			this.mapper = mapper;
		}

		@Override
		public void apply(T t) {
			super.val = this.mapper.apply(t, val);
		}
	}

	abstract class CummulativeAction<T, V> implements Action<V> {

		protected T val;

		public CummulativeAction(T initial) {
			val = initial;
		}

		public T get() {
			return val;
		}
	}
}
