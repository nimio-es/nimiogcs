package es.nimio.nimiogcs.functional;

public interface Action<T> {

	void apply(T a);
}
